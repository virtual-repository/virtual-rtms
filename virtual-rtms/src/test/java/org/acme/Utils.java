package org.acme;

import static dagger.ObjectGraph.*;

public class Utils {
	
	
	static class WithClause {
		
		Object testcase;
		
		public WithClause(Object testcase) {
			this.testcase=testcase;
		}
		
		void with(Object ... modules) {
			create(modules).inject(testcase);
		}
		
		void asModule() {
			create(testcase).inject(testcase);
		}
	}
	
	static WithClause init(Object testcase) {
		return new WithClause(testcase);
	}
	
	static void inject(Object testcase) {
		init(testcase).with(testcase);
	}
}
