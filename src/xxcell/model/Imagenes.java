/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author snak0
 */
public class Imagenes {
    private StringProperty nombre;
    
    public Imagenes(String name){
        this.nombre = new SimpleStringProperty(name);
    }

    public StringProperty getNombre() {
        return nombre;
    }

    public void setNombre(StringProperty nombre) {
        this.nombre = nombre;
    }
    
    public String getName(){
        return nombre.get();
    }
    
    
}
