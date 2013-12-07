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
		public void setId(int id) {
			this.id = id;
		}
		public void setName(String name) {
			this.name = name;
		}

		public ProductType(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public boolean isNew() {
			return id == -1;
		}
		
		public String toString()
		{
			return name;
		}
	}

