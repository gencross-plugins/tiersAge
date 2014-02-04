package com.mrprez.gencross.impl.tiersAge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mrprez.gencross.Personnage;
import com.mrprez.gencross.PoolPoint;
import com.mrprez.gencross.PropertiesList;
import com.mrprez.gencross.Property;
import com.mrprez.gencross.history.LevelToReachHistoryFactory;
import com.mrprez.gencross.value.IntValue;
import com.mrprez.gencross.value.StringValue;

public class TiersAgePerso extends Personnage {
	
	
	public void calculate(){
		super.calculate();
		if(getProperty("Race").getValue().getString().isEmpty()){
			errors.add("Vous devez choisir une race");
		}
		if(getProperty("Vocation").getValue().getString().isEmpty()){
			errors.add("Vous devez choisir une vocation");
		}
		if(phase.equals("Attributs")){
			calculateAttributs();
			calculateProfil();
		}
		if(phase.equals("Choix origine")){
			if(getProperty("Origine").getValue().getString().isEmpty()){
				errors.add("Vous devez choisir une origine");
			}
		}
		if(phase.equals("Choix enfance")){
			if(getProperty("Enfance").getValue().getString().isEmpty()){
				errors.add("Vous devez choisir une enfance");
			}
		}
		if(phase.equals("En vie")){
			for(PoolPoint poolPoint : pointPools.values()){
				if(poolPoint.getRemaining()<0){
					errors.add("Vous avez dépensé trop de "+poolPoint.getName());
				}
			}
		}
	}
	
	private void calculateProfil(){
		List<Integer> profilEquilibre = new ArrayList<Integer>(Arrays.asList(10,12,12,12,12,12,14));
		List<Integer> profilType = new ArrayList<Integer>(Arrays.asList(10,10,12,12,12,14,14));
		List<Integer> profilSpecialise = new ArrayList<Integer>(Arrays.asList(8,10,12,12,12,14,16));
		List<Integer> profilTresSpecialise = new ArrayList<Integer>(Arrays.asList(6,8,10,12,14,16,18));
		
		for(Property attribut : getProperty("Attributs").getSubProperties()){
			Integer v = attribut.getValue().getInt();
			int index = profilEquilibre.indexOf(v);
			if(index!=-1){
				profilEquilibre.remove(index);
			}
			index = profilType.indexOf(v);
			if(index!=-1){
				profilType.remove(index);
			}
			index = profilSpecialise.indexOf(v);
			if(index!=-1){
				profilSpecialise.remove(index);
			}
			index = profilTresSpecialise.indexOf(v);
			if(index!=-1){
				profilTresSpecialise.remove(index);
			}
		}
		if(!profilEquilibre.isEmpty() && !profilType.isEmpty() && !profilSpecialise.isEmpty() && !profilTresSpecialise.isEmpty()){
			errors.add("Vous devez respecter un profil: équilibré, typé, spécialisé ou trés spécialisé");
		}
	}
	
	private void calculateAttributs(){
		String race = getProperty("Race").getValue().getString();
		for(Property attribut : getProperty("Attributs").getSubProperties()){
			String minKey = "race."+race+"."+attribut.getName()+".min";
			if(appendix.containsKey(minKey)){
				int min = Integer.parseInt(appendix.getProperty(minKey));
				if(attribut.getValue().getInt()<min){
					errors.add("Vous devez avoir au moins "+min+" en "+attribut.getName());
				}
			}
			
			String maxKey = "race."+race+"."+attribut.getName()+".max";
			if(appendix.containsKey(maxKey)){
				int max = Integer.parseInt(appendix.getProperty(maxKey));
				if(attribut.getValue().getInt()>max){
					errors.add("Vous ne pouvez dépasser "+max+" en "+attribut.getName());
				}
			}
		}		
	}
	
	public void passToPhaseBonusVocation(){
		getProperty("Vocation").setEditable(false);
		String vocation = getProperty("Vocation").getValue().getString();
		String attName1 = appendix.getProperty("vocation."+vocation+".1");
		String attName2 = appendix.getProperty("vocation."+vocation+".2");
		for(Property attribut : getProperty("Attributs").getSubProperties()){
			attribut.setEditable(false);
		}
		getProperty("Attributs").getSubProperty(attName1).setEditable(true);
		getProperty("Attributs").getSubProperty(attName2).setEditable(true);
	}
	
	public void passToPhaseCulturel() {
		formulaManager.removeFormula("Attributs#Eveil#Points de communion");
		formulaManager.removeFormula("Attributs#Erudition#Points de sagesse");
		formulaManager.removeFormula("Attributs#Poésie#Points de rêve");
		formulaManager.removeFormula("Attributs#Noblesse#Points malice/majesté");
		formulaManager.removeFormula("Attributs#Prestance#Charme");
		formulaManager.removeFormula("Attributs#Grâce#Course");
		formulaManager.removeFormula("Attributs#Erudition#Langue maternelle");
		formulaManager.removeFormula("Attributs#Erudition#Westron");
		
		for(Property attribut : getProperty("Attributs").getSubProperties()){
			attribut.setEditable(false);
		}
		
		String race = getProperty("Race").getValue().getString();
		startCompetenceChoicePhase("culturel."+race);
		
	}
	
	public void passToChoixOrigine(){
		endCompetenceChoicePhase();
		String race = getProperty("Race").getValue().getString();
		for(String origine : appendix.getSubMap("origine."+race+".").values()){
			getProperty("Origine").getOptions().add(new StringValue(origine));
		}
		getProperty("Origine").setEditable(true);
	}
	
	public void passToPhaseOrigine(){
		String origine = getProperty("Origine").getValue().getString();
		getProperty("Origine").setEditable(false);
		startCompetenceChoicePhase("origine."+origine);
	}
	
	public void passToChoixEnfance(){
		endCompetenceChoicePhase();
		String race = getProperty("Race").getValue().getString();
		for(String enfance : appendix.getSubMap("enfance."+race+".").values()){
			getProperty("Enfance").getOptions().add(new StringValue(enfance));
		}
		getProperty("Enfance").setEditable(true);
	}
	
	public void passToPhaseEnfance(){
		String enfance = getProperty("Enfance").getValue().getString();
		getProperty("Enfance").setEditable(false);
		startCompetenceChoicePhase("enfance."+enfance);
	}
	
	public void passToPhaseVocation(){
		endCompetenceChoicePhase();
		String vocation = getProperty("Vocation").getValue().getString();
		startCompetenceChoicePhase("vocation.competence."+vocation);
	}
	
	public void passToPhaseEnVie(){
		endCompetenceChoicePhase();
		for(Property attribut : getProperty("Attributs").getSubProperties()){
			attribut.setEditableRecursivly(true);
			for(Property competence : attribut.getSubProperties()){
				if(formulaManager.isImpacted(competence.getAbsoluteName())){
					competence.setEditable(false);
				}
				competence.setHistoryFactory(new LevelToReachHistoryFactory(100, "Experience"));
			}
			for(Property option : attribut.getSubProperties().getOptions().values()){
				option.setHistoryFactory(new LevelToReachHistoryFactory(100, "Experience"));
			}
			attribut.setHistoryFactory(new TiersAgeAttributHistoryFactory("Experience"));
			attribut.getSubProperties().setFixe(false);
		}
		equipement();
		getProperty("Equipement").getSubProperties().setFixe(false);
	}
	
	private void endCompetenceChoicePhase(){
		Collection<String> baseCompetencesName = appendix.getSubMap("competence.base.").values();
		for(Property attribut : getProperty("Attributs").getSubProperties()){
			attribut.setEditableRecursivly(false);
			Iterator<Property> competenceIt = attribut.getSubProperties().iterator();
			while(competenceIt.hasNext()){
				Property competence = competenceIt.next();
				competence.setMin();
				if(competence.getValue().getInt()==0){
					if(!baseCompetencesName.contains(competence.getName())){
						competenceIt.remove();
					}
				}
			}
		}
	}
	
	private void startCompetenceChoicePhase(String appendixSuffix){
		Collection<String> baseCompetencesName = appendix.getSubMap("competence.base.").values();
		for(Property attribut : getProperty("Attributs").getSubProperties()){
			Map<String, String> competenceNames = appendix.getSubMap(appendixSuffix+"."+attribut.getName());
			for(String competenceName : competenceNames.values()){
				if(attribut.getSubProperty(competenceName)==null){
					Property option = attribut.getSubProperties().getOptions().get(competenceName);
					attribut.getSubProperties().add(option);
				}
				Property competence = attribut.getSubProperty(competenceName);
				competence.setEditable(true);
				competence.setMin();
				int max;
				if(baseCompetencesName.contains(competence.getName())){
					max = competence.getValue().getInt()+2;
				}else{
					max = Math.min(5, competence.getValue().getInt()+2);
				}
				competence.setMax(new IntValue(max));
			}
		}
		getPointPools().get("Création").add(15);
	}
	
	private void equipement(){
		PropertiesList equipementList = getProperty("Equipement").getSubProperties();
		for(Property attribut : getProperty("Attributs").getSubProperties()){
			for(Property competence : attribut.getSubProperties()){
				if(competence.getValue().getInt()>=4){
					String equipementName = appendix.getProperty("equipement."+attribut.getName()+"."+competence.getName());
					if(equipementName!=null){
						Property newEquipement = equipementList.getDefaultProperty().clone();
						newEquipement.setName(equipementName);
						equipementList.add(newEquipement);
					}
				}
			}
		}
		
		String vocation = getProperty("Vocation").getValue().getString();
		String equipementVocationString = appendix.getProperty("equipement.vocation."+vocation);
		String equipementVocation[] = equipementVocationString.split(";");
		for(int i=0; i<equipementVocation.length; i++){
			String equipementName = equipementVocation[i].trim();
			if(getProperty("Equipement").getSubProperty(equipementName)==null){
				Property newEquipement = equipementList.getDefaultProperty().clone();
				newEquipement.setName(equipementName);
				equipementList.add(newEquipement);
			}
		}
	}

	

}
