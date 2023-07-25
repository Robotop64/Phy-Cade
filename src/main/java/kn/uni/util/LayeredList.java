package kn.uni.util;

import java.util.ArrayList;

public class LayeredList <T, E extends Enum <E>>
{
  //Array containing all objects
  public ArrayList <T>       list;
  //Array containing the offsets between the objects and the layers
  public ArrayList <Integer> offsets;
  //Array containing the types of the layers
  public E[]                 types;

  public LayeredList (Class <E> enumClass)
  {
    list = new ArrayList <>();
    offsets = new ArrayList <>();
    types = enumClass.getEnumConstants();
    initializeOffsets();
  }

  private void initializeOffsets ()
  {
    int numLayers = types.length;
    for (int i = 0; i < numLayers; i++)
    {
      offsets.add(0);
    }
  }

  /**
   * Add an object to the list at the end of the layer
   * @param layer The layer, given by a predefined enum, to add the object to
   * @param obj The object to add
   * @Example:
   * Layers:       [A, B, C, D, E] <p>
   * Initial List: [A1,B1,C1,  E1] <p>
   * add(D, D1)<p>
   * Layers:[A, B, C, D, E]<p>
   * =>     [A1,B1,C1,D1,E1]<p>
   * add(B, B2)<p>
   * Layers:[A, B,    C, D, E]<p>
   * =>     [A1,B1,B2,C1,D1,E1]<p>
   */
  public void add (E layer, T obj)
  {
    int layerStart     = 0;
    int layerEnd       = 0;
    int insertPosition = 0;

    layerStart = offsets.get(layer.ordinal());

    if (layer.ordinal() == types.length - 1)
      layerEnd = list.size() - 1;
    else
      layerEnd = offsets.get(layer.ordinal() + 1) - 1;


    if (list.size() > 0)
      insertPosition = layerEnd + 1;

    list.add(insertPosition, obj);

    for (int i = layer.ordinal() + 1; i < offsets.size(); i++)
    {
      offsets.set(i, offsets.get(i) + 1);
    }
  }

  /**
   * Get all objects of a specific layer
   * @param layer The layer, given by a predefined enum, to get the objects from
   * @return An ArrayList containing all objects of the given layer
   * @Example:
   * Layers:       [A, B,       C, D, E]<p>
   * Initial List: [A1,B1,B2,B3,C1,D1,E1]<p>
   * return get(B): [B1,B2,B3]
   */
  public ArrayList <T> get (E layer)
  {
    int layerStart = 0;
    int layerEnd   = 0;

    layerStart = offsets.get(layer.ordinal());

    if (layer.ordinal() == types.length - 1)
      layerEnd = list.size();
    else
      layerEnd = offsets.get(layer.ordinal() + 1);

    return list.subList(layerStart, layerEnd).stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
  }
}
