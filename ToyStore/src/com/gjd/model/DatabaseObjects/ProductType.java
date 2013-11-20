package com.gjd.model.DatabaseObjects;

public class ProductType {

		private int id;
		private String name;
		
		public int getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		
		public ProductType(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}

