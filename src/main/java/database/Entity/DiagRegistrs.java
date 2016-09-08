package database.Entity;

import base.RegTypes;

public class DiagRegistrs extends Registrs{
    private String description;

    public DiagRegistrs (String description) {     /*для текстовых сообщений*/
        super(0, 0, RegTypes.NONE,0);
        this.description=description;
    }

    @Override
    public String toString() {
        return  description;
    }
}
