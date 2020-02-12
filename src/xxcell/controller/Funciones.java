/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xxcell.controller;

import java.util.List;
import javafx.scene.input.KeyEvent;

public class Funciones {
	public static final String dispatchWindowClosingActionMapKey = "com.spodding.tackline.dispatch:WINDOW_CLOSING";
	private Integer counter=0;
	String value="";
	
	

	boolean IsNumeric(char caracter){
		boolean result = false;
		if(caracter == '0')
			result = true;
		else if(caracter == '1')
			result = true;
		else if(caracter == '2')
			result = true;
		else if(caracter == '3')
			result = true;
		else if(caracter == '4')
			result = true;
		else if(caracter == '5')
			result = true;
		else if(caracter == '6')
			result = true;
		else if(caracter == '7')
			result = true;
		else if(caracter == '8')
			result = true;
		else if(caracter == '9')
			result = true;
		
		return result;
	}
	
	boolean IsInteger(String cadena){
		boolean result = true;
		int cuenta;
		if(cadena.isEmpty())
			result = false;
		else
			for(cuenta = 0; cuenta<cadena.length(); cuenta++)
				if(!IsNumeric(cadena.charAt(cuenta))){
					result = false;
					break;
				}
		return result;
	}
	
	boolean IsDecimal(String cadena){
		boolean result = true;
		boolean punto = false;
		
		int cuenta;
		if(cadena.isEmpty())
			result = false;
		else
			for(cuenta = 0; cuenta < cadena.length(); cuenta++)
				if(cadena.charAt(cuenta)=='.')
					if(punto){
						result = false;
						break;
					}
					else
						punto = true;
				else
					if(!IsNumeric(cadena.charAt(cuenta))){
						result = false;
						break;
					}
		return result;
	}
	
	public String getStringofNumber(Integer $num){
		this.counter = $num;
		return doThings($num);
	}
	
	private String doThings(Integer _counter){
		if(_counter>2000000)
			return "DOS MILLONES";
		switch(_counter){
		case 0: return "CERO";
		case 1: return "UN";
		case 2: return "DOS";
		case 3: return "TRES";
		case 4: return "CUATRO";
		case 5: return "CINCO";
		case 6: return "SEIS";
		case 7: return "SIETE";
		case 8: return "OCHO";
		case 9: return "NUEVE";
		case 10: return "DIEZ";
		case 11: return "ONCE";
		case 12: return "DOCE";
		case 13: return "TRECE";
		case 14: return "CATORCE";
		case 15: return "QUINCE";
		case 20: return "VEINTE";
		case 30: return "TREINTA";
		case 40: return "CUARENTA";
		case 50: return "CINCUENTA";
		case 60: return "SESENTA";
		case 70: return "SETENTA";
		case 80: return "OCHENTA";
		case 90: return "NOVENTA";
		case 100: return "CIEN";
		
		case 200: return "DOSCIENTOS";
		case 300: return "TRESCIENTOS";
		case 400: return "CUATROCIENTOS";
		case 500: return "QUINIENTOS";
		case 600: return "SEISCIENTOS";
		case 700: return "SETECIENTOS";
		case 800: return "OCHOCIENTOS";
		case 900: return "NOVECIENTOS";
		
		case 1000: return "MIL";
		
		case 1000000: return "UN MILLON";
		case 2000000: return "DOS MILLONES";
		}
		if(_counter<20){
			return "DIECI"+doThings(_counter-10);
		}
		if(_counter<30){
			return "VEINTI"+doThings(_counter-20);
		}
		if(_counter<100){
			return doThings((int)(_counter/10)*10) + " Y "+ doThings(_counter%10);
		}
		if(_counter<200){
			return "CIENTO " + doThings(_counter-100);
		}
		if(_counter<1000){
			return doThings((int)(_counter/100)*100) + " Y "+ doThings(_counter%100);
		}
		if(_counter<2000){
			return "MIL " + doThings(_counter%1000);
		}
		if(_counter<1000000){
			String var;
			var = doThings((int)(_counter/1000)) + " MIL";
			if(_counter % 1000!=0){
				var += " " + doThings(_counter % 1000);
			}
			return var;
		}
		if(_counter<2000000){
			return "UN MILLON " + doThings(_counter % 1000000);
		}
		return "";
	}

        
        static String jumpTo(String keyPressed, String currentlySelected, List<String> items) {
        String key = keyPressed.toUpperCase();   
        if (key.matches("^[A-Za-z0-9_.]+$")) {   //   // ^[A-Z]$
            System.out.println("Letter found " + key);
            // Only act on letters so that navigating with cursor keys does not
            // try to jump somewhere.
            boolean letterFound = false;
            boolean foundCurrent = currentlySelected == null;
            for (String s : items) {
                if (s.toUpperCase().startsWith(key)) {
                    letterFound = true;
                    if (foundCurrent) {
                        System.out.println("s " + s);
                        return s;
                    }
                    foundCurrent = s.equals(currentlySelected);
                }
            }
            if (letterFound) {
                return jumpTo(keyPressed, null, items);
            }
        }
        return null;
    }
        
}