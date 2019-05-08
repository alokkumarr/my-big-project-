package sncr.xdf.context;

public enum RequiredNamedParameters{


    Input,
    Output,
    Rejected;


    public String toString(){
        return this.name().toLowerCase();
    }


}
