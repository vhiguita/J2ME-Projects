package com.encuestas;

import java.util.*;

public class ToDoTest {
	public static void main(String[] args) {
		ToDoList list = new ToDoList("com.mysql.jdbc.Driver",
				"jdbc:mysql://localhost/todo?user=root&password=hikaru");

		System.out.println("The to-do list contains " + list.getItemCount() +
											 "items:");
		Iterator it = list.getToDoItems().iterator();
		while (it.hasNext()) {
			System.out.println(" - " + it.next());
		}

		list.addItem("Another todo item.", "2131221", "blah ");
		list.addItem("And yet another.", "1378123", "blah ");

		System.out.println("The to-do list contains " + list.getItemCount() +
											 " items:");
		it = list.getToDoItems().iterator();
		while (it.hasNext()) {
			System.out.println(" - " + it.next());
		}
	}
}
