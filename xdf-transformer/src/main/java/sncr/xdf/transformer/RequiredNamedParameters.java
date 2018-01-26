package sncr.xdf.transformer;

public enum RequiredNamedParameters{


    Input,
    Output,
    Rejected;


    public String toString(){
        return this.name().toLowerCase();
    }


}
