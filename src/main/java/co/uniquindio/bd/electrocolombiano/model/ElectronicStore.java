package co.uniquindio.bd.electrocolombiano.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ElectronicStore {
    private static ElectronicStore singleton;
    private SystemUser currentUser;
    private Sale sale;

    public static ElectronicStore getSingleton() {
        if(singleton == null){
            singleton = new ElectronicStore();
        }
        return singleton;
    }
}
