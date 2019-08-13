package es.manuel.vera.silvestre.util;

import es.manuel.vera.silvestre.modelo.ArchetypeItem;
import es.manuel.vera.silvestre.modelo.Demand;
import es.manuel.vera.silvestre.modelo.InventoryItem;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ItemUtil{

    private static final Logger LOGGER = Logger.getLogger(AppUtil.class);

    public static Map<ArchetypeItem,Integer> getNeededItems(){
        List<Long> missingEquipmentIds = AppUtil.getMissingEquipmentIds();

        Map<ArchetypeItem,Integer> neededItems = missingEquipmentIds.stream()
            .map(ItemUtil::findRecipe)
            .map(ItemUtil::getNeededItems)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(item -> item, item -> 1, Integer::sum));

        neededItems.replaceAll(ItemUtil::subtractsFromInventory);

        return neededItems.entrySet().stream()
            .filter(e -> e.getValue() > 0)
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    private static int subtractsFromInventory(
        ArchetypeItem needed, Integer number){
        Map<Long,InventoryItem> inventory = AppUtil.getInventoryMap();
        if(!inventory.containsKey(needed.getArchetypeId())){
            return number;
        }

        InventoryItem item = inventory.get(needed.getArchetypeId());
        if(number <= item.getQuantity()){
            inventory.replace(needed.getArchetypeId(), item.toBuilder().quantity(item.getQuantity() - number).build());
            return 0;
        }

        inventory.replace(needed.getArchetypeId(), item.toBuilder().quantity(0).build());
        return number - item.getQuantity();
    }

    private static List<ArchetypeItem> getNeededItems(Demand demand){
        ArchetypeItem item = findRecipe(demand.getArchetypeId());
        List<ArchetypeItem> neededItems = getNeededItems(item);

        if(demand.getCount() == 1){
            return neededItems;
        }

        return IntStream.range(0, demand.getCount())
            .mapToObj(i -> neededItems)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private static List<ArchetypeItem> getNeededItems(ArchetypeItem item){
        if(item.getSources().size() > 0 || item.getRecipe() == null){
            return Collections.singletonList(item);
        }

        return item.getRecipe().getDemands().stream()
            .map(ItemUtil::getNeededItems)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private static ArchetypeItem findRecipe(Long id){
        List<ArchetypeItem> recipes = AppUtil.getRecipes();
        ArchetypeItem item = recipes.stream()
            .filter(recipe -> recipe.getArchetypeId() == id).findAny()
            .orElse(null);

        if(item != null){
            return item;
        }

        Map<Long,InventoryItem> inventory = AppUtil.getInventoryMap();
        if(inventory.containsKey(id)){
            InventoryItem inventoryItem = inventory.get(id);
            LOGGER.warn(
                "findRecipe: recipe not found for item with id " + id + ", but found in inventory: " + inventoryItem);
            return new ArchetypeItem(inventoryItem.getType(), inventoryItem.getSymbol(), inventoryItem.getName(),
                inventoryItem.getFlavor(), inventoryItem.getArchetypeId(), inventoryItem.getStars(), null,
                Collections.emptyList());
        }

        LOGGER.warn(
            "findRecipe: recipe not found for item with id " + id);
        return new ArchetypeItem(-1, "", "Unknown: " + id, "", id, -1, null, Collections.emptyList());
    }
}
