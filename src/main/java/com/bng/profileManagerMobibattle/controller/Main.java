package com.bng.fynder.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Main {

	public static void main(String args[]) {

//		try (InputStream is = new FileInputStream("/software/test.txt");) {
//			// new input stream created
//
//			// read and print characters one by one
//			System.out.println("Char : " + (char) is.read());
//			System.out.println("Char : " + (char) is.read());
//			System.out.println("Char : " + (char) is.read());
//			is.skip(2);
//			// mark is set on the input stream
//			is.mark(1);
//
//			System.out.println("Char : " + (char) is.read());
//			System.out.println("Char : " + (char) is.read());
//
//			if (is.markSupported()) {
//
//				// reset invoked if mark() is supported
//				is.reset();
//				System.out.println("Char : " + (char) is.read());
//				System.out.println("Char : " + (char) is.read());
//			}
//
//		} catch (Exception e) {
//			// if any I/O error occurs
//			e.printStackTrace();
//		} finally {
//
//		}
		TimeZone timeZone = TimeZone.getTimeZone("GMT");
		Long localTime = System.currentTimeMillis();
		System.out.println("time="+localTime);
		
		Date date = new Date(localTime);
	  SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mma z");
	  formatter.setTimeZone(timeZone);
	  
	  System.out.println(formatter.format(date));
	  
	  System.out.println("date="+new Date());
	  

	}

}
