package com.xorinc.chem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChemString {

	public static final String NO_SPACE = "--nospace";
	public static final String TILED = "--notiled";
	
	public static final HashMap<String, String> symbols = new HashMap<String, String>();
	public static final HashMap<String, String> numbers = new HashMap<String, String>();
	public static final HashMap<String, String> weights = new HashMap<String, String>();
	public static final HashMap<Character, String[]> chars = new HashMap<Character, String[]>();
	
	static {
		
		for(String el : Constants.pTable.split(" ")){			
			String[] data = el.split(";");
			
			symbols.put(data[0].toLowerCase(), data[1].toLowerCase());
			numbers.put(data[0].toLowerCase(), data[2]);
			weights.put(data[0].toLowerCase(), data[3]);
		}
		
		for(String[] l : Constants.charset){
			
			chars.put(l[0].charAt(0), Arrays.copyOfRange(l, 1, l.length));			
		}
		
	}
	
	public static void main(String... args) throws IOException{
		
		if(args.length == 0){
			System.out.println("Enter a phrase or file path starting with `$'");
			System.out.println("Options: --nospace --notiled");
			return;
		}
		
		List<String> options  = new ArrayList<String>();
		int i = 0;
		
		try{
			for(i = 0; args[i].startsWith("--"); i++){
				
				options.add(args[i]);
				
			}
		}		
		catch(ArrayIndexOutOfBoundsException e) {}
		finally {
			
			args = Arrays.copyOfRange(args, i, args.length);
			
		}
		
		if(args.length == 0){
			System.out.println("Enter a phrase or file path starting with `$'");
			System.out.println("Options: --nospace --notiled");
			return;
		}
		
		if(args[0].startsWith("$")){
			
			String path = "";
			
			for(String s : args){
				
				path += s + " ";				
			}
			
			path = path.trim().substring(1);
			
			File f = new File(path);
			
			if(!f.exists()){
				System.out.println(path + " does not exist!");
				return;
			}
			
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(f));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			
			try{
								
				while(true){
					
					String s;
					try {
						s = br.readLine();
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					
					if(s == null)
						break;
					else{
					
						String result = toSymbols(s);
						
						if(result != null)
							print(result, options.contains(NO_SPACE), options.contains(TILED));
						
					}
					
				}
			}
			finally { br.close(); }
		}
		else{
			
			String test = "";
			
			for(String a : args){
				test += a + " ";
			}
			
			test = test.trim();
						
			String result = toSymbols(test);
			
			if(result == null)
				System.out.println("Input is not chemical :(");
			else
				print(result, options.contains(NO_SPACE), options.contains(TILED));
			
		}		
	}
	
	public static String toSymbols(String s){
				
		s = s.toLowerCase();		
		s = s.replaceAll("[^a-z]", "");
		
		try {
			return node(s, "", 0).trim();
		} catch (NodeException e) {
			return null;
		}
		
	}
	
	private static String node(String s, String result, int i) throws NodeException{
		
		if(s.equals(result.replaceAll("[^a-z]", "")))
			return result;
		
		if(s.length() - i - 2 >= 0 && symbols.containsKey(s.substring(i, i + 2))){
			
			try{
				result = node(s, result + " " + s.substring(i, i + 2), i + 2);
				i += 2;
				return result;
			}
			catch(NodeException e){}
		}
		
		if(symbols.containsKey(s.substring(i, i + 1))){
			
			result = node(s, result + " " + s.substring(i, i + 1), i + 1);
			i++;
			return result;
		}
		
		throw new NodeException();		
	}
	
	public static String capitalize(String s){
		
		String result = "";
		
		for(String t : s.split(" ")){
			
			result += t.substring(0, 1).toUpperCase() + t.substring(1).toLowerCase() + " ";
			
		}
		
		return result.trim();
		
	}
	
	public static String toNames(String s){
		
		String result = "";
		
		for(String t : s.split(" ")){
			
			result += symbols.get(t) + " ";
			
		}
		
		return result;
		
	}
	
	public static String[] generateTableTile(String element){
		
		//width = 15
		//height = 8
		
		String name = capitalize(symbols.get(element));
		List<String> list = new ArrayList<String>();
		
		list.add("+---------------------+");
		list.add("|         ***         |".replace("***", pad(numbers.get(element), 3))); //TODO: complete tile feature
		
		for(String s : bigSymbol(element))
		list.add("|*********************|".replace("*********************", pad(s, 21)));
			
		list.add("|                     |");
		list.add("|wwwwwwwwwwwwwwwwwwwww|".replace("wwwwwwwwwwwwwwwwwwwww", pad(name, 21)));
		list.add("|                     |");
		list.add("|       *******       |".replace("*******", pad(weights.get(element), 7)));
		list.add("+---------------------+");
		
		return list.toArray(new String[list.size()]);
	}
	
	public static String pad(String s, int i){
		
		if(s.length() > i)
			return s.substring(0, i + 1);
		
		if(s.length() == i)
			return s;
		
		boolean front = true;
		
		while(s.length() < i){
			
			if(front)
				s = " " + s;
			else
				s += " ";
			
			front = !front;
			
		}
		
		return s;
	}
	
	public static String[] bigSymbol(String s){
		
		if(s.length() == 0)
			return null;
				
		String[] result = chars.get(s.toUpperCase().charAt(0)).clone();
		
		if(s.length() == 1)
			return result;
			
		String[] second = chars.get(s.toLowerCase().charAt(1));
		
		for(int i = 0; i < second.length; i++){
			
			result[i] += second[i];
						
		}
		
		return result;
		
	}
	
	public static void print(String result, boolean nospace, boolean notiled){
		
		if(!notiled){
			
			List<String[]> tiles = new ArrayList<String[]>(); 
			
			for(String el : result.split(" ")){
				
				tiles.add(generateTableTile(el));
			}
			
			for(int i = 0; i < tiles.get(0).length; i++){
				
				for(String[] tile : tiles)
					System.out.print(tile[i] + " ");
				
				System.out.println();
			}
			
		}
		
		else {
			System.out.println((nospace ? capitalize(result).replaceAll(" ", "") : capitalize(result)) + " : " + capitalize(toNames(result)));
		}
	}
	
	private static class NodeException extends Exception{}
}


