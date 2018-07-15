package com.seebaldtart.projectinventoryapp.data;
public class InventoryContract {
    public static class BookEntry {
        public static String TABLE_NAME = "books";
        public static String PRODUCT_ID = "_id";
        public static String PRODUCT_NAME = "product_name";
        public static String PRICE = "price";
        public static String QUANTITY = "quantity";
        public static String SUPPLIER_NAME = "supplier_name";
        public static String SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}