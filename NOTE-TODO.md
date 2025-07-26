
improve option system: weird stuff => pointer/casting stuff 
```C
typedef enum{
    BOOL,
    INT,
    FLOAT,
    STRING,
    CHOICE,
    RANGE,
    COUNT
}OptionsType;

typedef struct{
    OptionType type,
    std::string name,
    bool visible,
    bool editable,
}OptionHeader;

typedef struct{
    OptionHeader header,
    bool value
}OptionBool;

typedef struct{
    OptionHeader header,
    int value
}OptionInt;

typedef struct{
    OptionHeader header,
    float value
}OptionFloat;

typedef struct{
    OptionHeader header,
    std::string value
}OptionString;

typedef struct{
    OptionHeader header,
    OptionsType subtype,
    std::vector<T> choices
}OptionChoice<T>;

typedef struct{
    OptionHeader header,
    int value,
    int min,
    int max,
    int step
}OptionIntRange;

typedef struct{
    OptionHeader header,
    float value,
    float min,
    float max,
    float step
}OptionFloatRange;

OptionType get_type(OptionHeader *h){
  return h->type;
}

std::optional<OptionType> get_subtype(OptionHeader *h){
  if(h->type == CHOICE){
    return h->subtype;
  }
  return std::nullopt;
}

use_function(OptionHeader *h){
  switch(h.Type){
    case BOOL:
      OptionBool *bool_option = (OptionBool *)h;
      ...
    case INT:
      OptionInt *int_option = (OptionInt *)h;
      ...
    ...
    case CHOICE:
      OptionChoice *choice_option = (OptionChoice *)h;
      switch(choice_option->subtype){
        case BOOL:
        ...
      }
  }
}

void main(){
    OptionBool my_option = {
        .header = {
            .type = BOOL,
            .name = "Enable Feature",
            .visible = true,
            .editable = true
        },
        .value = true
    };

    use_function((OptionHeader *)&my_option);
}
```