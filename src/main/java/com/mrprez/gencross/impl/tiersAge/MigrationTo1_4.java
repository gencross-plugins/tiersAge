package com.mrprez.gencross.impl.tiersAge;

import com.mrprez.gencross.Version;
import com.mrprez.gencross.listener.PassToNextPhaseListener;
import com.mrprez.gencross.listener.dummy.DummyPassToNextPhaseListener;
import com.mrprez.gencross.migration.MigrationPersonnage;
import com.mrprez.gencross.migration.Migrator;

public class MigrationTo1_4 implements Migrator {

	@Override
	public MigrationPersonnage migrate(MigrationPersonnage migrationPersonnage) throws Exception {
		for(PassToNextPhaseListener listener : migrationPersonnage.getPassToNextPhaseListeners()) {
			DummyPassToNextPhaseListener dummyListener = (DummyPassToNextPhaseListener) listener;
			if(dummyListener.getClassName().equals("com.mrprez.gencross.listener.impl.ChangeDefaultHistoryListener")){
				dummyListener.setClassName("com.mrprez.gencross.listener.impl.ChangeHistoryFactoryListener");
				dummyListener.getArgs().put("class", "com.mrprez.gencross.history.ProportionalHistoryFactory");
				dummyListener.getArgs().put("factor", "2");
			}
		}
		
		migrationPersonnage.getPluginDescriptor().setVersion(new Version(1,4));
		return migrationPersonnage;
	}

}
