package com.mrprez.gencross.impl.tiersAge;

import com.mrprez.gencross.Property;
import com.mrprez.gencross.renderer.Renderer;
import com.mrprez.gencross.value.Value;

public class TiersAgeRenderer extends Renderer {

	@Override
	public String displayValue(Value value) {
		int v = value.getInt();
		int d = v/10;
		int r = v - (d*10);
		if(r==2){
			d--;
			r = 12;
		}
		StringBuilder sb = new StringBuilder();
		if(d>0){
			sb.append(d).append("D10");
			if(r>0){
				sb.append("+");
			}
		}
		if(r>0){
			sb.append("D").append(r);
		}
		return sb.toString();
	}

	@Override
	public String displayValue(Property property) {
		return displayValue(property.getValue());
	}
	
	

}
