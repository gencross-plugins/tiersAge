package com.mrprez.gencross.impl.tiersAge;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;

import com.mrprez.gencross.history.HistoryFactory;
import com.mrprez.gencross.value.Value;

public class TiersAgeAttributHistoryFactory extends HistoryFactory {

	public TiersAgeAttributHistoryFactory(String pointPollName){
		super(pointPollName);
	}
	
	public TiersAgeAttributHistoryFactory(Element element) {
		super(element);
	}

	@Override
	public Map<String, String> getArgs() {
		return new HashMap<String, String>();
	}

	@Override
	public int getCost(Value oldValue, Value newValue, int action) {
		int cost = 0;
		int min = Math.min(newValue.getInt(), oldValue.getInt());
		int max = Math.max(newValue.getInt(), oldValue.getInt());
		for(int i=min+2; i<=max; i=i+2){
			cost = cost + (i-6) / 2 * 1000;
		}
		return (int) (Math.signum(newValue.getInt() - oldValue.getInt())*cost);
	}

	@Override
	public void setArgs(Map<String, String> map) {
		;
	}

	@Override
	public HistoryFactory clone() {
		return new TiersAgeAttributHistoryFactory(pointPool);
	}

}
