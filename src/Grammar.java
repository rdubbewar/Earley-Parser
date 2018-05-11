////////////////////////////////////////////////////////////////////////////////
//
//      MP 4 - cs585
//
//      Paul Chase
//
//      This implements a cfg style grammar
////////////////////////////////////////////////////////////////////////////////

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

/**
 * @author rucha
 */
//this class implements the third assignment for CS585
public class Grammar
{
	private Vector productions;
	private Vector nonTerminals;
	
	//this reads the grammar in from a file
	public Grammar(String f) throws Exception
	{
		productions = new Vector();
		productions.clear();
		nonTerminals = new Vector();
		nonTerminals.clear();
		//load the file
		BufferedReader br = new BufferedReader(new FileReader(f));
		Production p;
		String str = br.readLine();
		String rule[];
		while(str!=null)
		{
			rule = str.split("\t");
			p = new Production();
			p.probability = (new Float(rule[0])).floatValue();
			p.left = rule[1];
			p.right = rule[2].split(" ");
			p.dot = 0;
			p.start = 0;
			
			productions.add(p);
			
			addNonTerminal(rule[1]);
			str = br.readLine();
		}
	}

	/**
	 * Add NonTerminal if they are not present
	 * @param s
	 */
	private void addNonTerminal(String s)
	{
		for(int i=0;i<nonTerminals.size();i++)
		{ 
			if(((String)nonTerminals.get(i)).compareTo(s)==0){
				
				return;
			}
		}
		
		nonTerminals.add(s);
	}
/**
 *  Checking a non terminal
 * @param s
 * @return
 */
	private final boolean isNonTerminal(String s)
	{
		//return true if it's a non-terminal
		for(int i=0;i<nonTerminals.size();i++)
		{
			if(((String)nonTerminals.get(i)).compareTo(s)==0){
				
				System.out.println(s+" NonTerminal? Yes");
				return true;
			}
		}
		
	
		System.out.println(s+" NonTerminal? No");
		return false;
	}

	/**
	 * Prediction function
	 * @param v
	 * @param p
	 * @param pos
	 */
	private final void predict(Vector v, Production p,int pos)
	{
		Vector prods = getProds(p.right[p.dot]);
		Production q,r;
		
		for(int j=0;j<prods.size();j++)
		{
			r = (Production)prods.get(j);
			q = new Production(r);
			q.dot = 0;
			q.start = pos;
			
			addProd(v,q);
		}
	}

	/**
	 * This scans the production p with vector v
	 * @param v
	 * @param p
	 * @param s
	 * @return
	 */
	private final boolean scan(Vector v, Production p, String s)
	{
		Production q;
		
		System.out.println("scan:"+p.toString());
		if(p.right[p.dot].compareTo(s)==0)
		{
			
			System.out.println("Scan: Matched! Incrementing dot");
			
			q = new Production(p);
			q.dot = q.dot + 1;
			
			addProd(v,q);
			return true;
		}
	
		System.out.println("Scan: Not Matched!");
		return false;
	}
/**
 * Attached the word in production
 * @param cols
 * @param cur
 * @param p
 * @param back
 */
	
	private final void attach(Vector cols, Vector cur, Production p, HashMap back)
	{
		
		Vector col;
		Production q,r;
		String s = p.left;
		boolean match = false;
		
		col = (Vector)cols.get(p.start);
			
		System.out.println("attach:"+p.toString());
		for(int j=0;j<col.size();j++)
		{
			q = (Production)col.get(j);
			
			System.out.println("\tChecking for q = "+q);
			if(q.right.length > q.dot)
				if(q.right[q.dot].compareTo(s)==0)
				{	
					System.out.println("\tAttach Matched! Incrementing dot");
					r = new Production(q);
					r.dot = r.dot + 1;
					
					if(!r.childExists()){
						System.out.println("Child Exists? false");
						System.out.println("Spawning "+r.right.length);
						r.spawn(q.right.length);
					}
					else{
						System.out.println("Child Exists? true, Child Size = "+(r.getChildSize()+r.start+p.getChildSize()));
					}

					System.out.println("Spawning "+(r.dot-1)+", "+p.toString());
					
					r.spawn(r.dot-1, p);
					
					System.out.println("\tInvoking add prod from attach for cur");
					addProd(cur,r);
				}
		}
	}
	
	
	/**
	 * This parses the production 
	 * @param sent
	 * @return
	 */
	public final Production parse(String sent[])
	{
	
		System.out.println("Parse invoked");
		//this is a vector of vectors, storing the columns of the table
		Vector cols = new Vector();	cols.clear();
		//this is the current column; a vector of production indices
		Vector cur = new Vector();	cur.clear();
		//this is the next column; a vector of production indices
		Vector next = new Vector();	next.clear();	
		//Adding back pointer
		//this is the back pointer; a vector of production indices
		HashMap back = new HashMap();	back.clear();
		HashMap bkPtr = new HashMap();	bkPtr.clear();
		//add the first symbol
		cur.add((Production)getProds("ROOT").get(0));
		Production p;
		for(int pos=0;pos<=sent.length;pos++)
		{
			System.out.println("Pos = " + pos);
			int i=0;
			boolean match = false;
			
			while(i!=cur.size())
			{
				p = (Production)cur.get(i);
				
				System.out.println("\nIteration Begins("+i+")! Current p: "+p);
				if(p.right.length > p.dot)
				{	
					if(sent.length == pos)
					{
						
						System.out.println("Match is true!sent.length == pos");
						match = true;
						
					} else{
						if(isNonTerminal(p.right[p.dot]))
						{
							
							System.out.println("Predicting (cur) "+p+" at "+pos);
							
							predict(cur,p,pos);
						}else{
							
						    System.out.println("scan (next): " + p.toString() + " ("+pos+")= " + sent[pos]);
						    if(scan(next,p,sent[pos])) {
							System.out.println("  Found: " + sent[pos]);
							match = true;
						    }
						}
					}
				} else {	
					
					System.out.println("Attaching (cols)(cur) "+p);
					
				    attach(cols,cur,p,back/*,pos, i, bckPtr*/);
					
				    if(sent.length == pos)
					{
					    match = true;
						
						System.out.println("Else Match True!");
					}
				}
				i++;
				
				
				System.out.println("Incrementing i");
				
				if(i%100 == 0)
					System.out.print(".");
			}
			System.out.println("Match = " + match);
			
			System.out.println("Adding cur to cols");
			cols.add(cur);
			if(!match)
			{
			  //  printTable(cols,sent);
			    System.out.println("Failed on: "+ cur);
			    return null;
			}
			
			
			System.out.println("Cur");
			print(cur);
			System.out.println("Next");
			print(next);
			
			System.out.println("Back");
			print(back);
			System.out.println("Incrementing pos");
			cur = next;
			next = new Vector();	next.clear();
			System.out.println();
		}
		
		

		
		cur = (Vector)cols.get(cols.size()-1);
		Production finished = new Production((Production)getProds("ROOT").get(0));
		finished.dot = finished.right.length;
		boolean parsed = false;
		for(int i=0;i<cur.size();i++)
		{
			p = (Production)cur.get(i); 
			if(p.equals(finished))
			{
				parsed = true;
				
			}
		}
		if(parsed){
			System.out.println("Finished right: "+finished.right[0]);
			Vector finalProds = getFinalProds(cols);
			Production pFinal = new Production();
			pFinal = parsed_loop(finalProds, pFinal);
			return pFinal;
		}
		else{
			return null;
		}
	}

	private Production parsed_loop(Vector finalProds, Production pFinal) {
		for(int i = 0; i<finalProds.size(); i++){
			Production s = (Production) finalProds.get(i);
			System.out.println("s left: "+s.left);
			if(s.left.equals("s")){
				pFinal = s;
				
			}
			
		}
		return pFinal;
	}

	private boolean scanAdd(Vector col, Production b, Vector backLinks){
		for (int j=0; j<col.size(); j++){
			Production p = (Production) col.get(j);
			if(p.left == b.right[b.dot-1])
			{
				if(p.right.length==p.dot){
				
					System.out.println("Adding "+p);
					addProd(backLinks, p);
					return true;
				}
			}
		}
		return false;
		
	}

	/**this prints the table in a human-readable fashion.
	 * format is one column at a time, lists the word in the sentence
	 * and then the productions for that column.
	 * @param cols The columns of the table
	 * @param sent the sentence
	 */
	private final void printTable(Vector cols,String sent[])
	{
		Vector col;
		
		tablePrint(cols, sent);
	}

	private void tablePrint(Vector cols, String[] sent) {
		Vector col;
		for(int i=0;i<cols.size();i++)
		{
			col = (Vector)cols.get(i);
			
			if(i>0)
			{
				System.out.println("\nColumn "+i+": "+sent[i-1]+"\n------------------------");
			}else{
				System.out.println("\nColumn "+i+": ROOT\n------------------------");
			}
			
			for(int j=0;j<col.size();j++)
			{
				System.out.println(((Production)col.get(j)).toString());
			}
		}
	}
/**
 * Adding Vector in Production
 * @param v
 * @param p
 */
	
	private final void addProd(Vector v, Production p)
	{
		
		for(int i=0;i<v.size();i++)
			if(((Production)v.get(i)).equals(p)){
				
				Production q = (Production)v.get(i);
				System.out.println("addProd: Duplicate to cur |"+p);
				System.out.println("\n addProd: Existing Child? "+q.childExists());
				System.out.println("\n addProd: Incoming Child? "+p.childExists());
				return;
			}
		v.add(p);
		
	}

	
	private final Vector getFinalProds(Vector cols)
	{
		Vector cur;
		Vector prods = new Vector();	prods.clear();
		Production p;
		
		System.out.println("getFinalProds invoked!");
		for(int i=0; i<cols.size(); i++)
		{
			cur = (Vector)cols.get(i);
			prodFinal(cols, cur, prods, i);
		}
	
		return prods;
	}

	private void prodFinal(Vector cols, Vector cur, Vector prods, int i) {
		Production p;
		for(int j=0;j<cur.size();j++)
		{
			p = (Production)cur.get(j);
			if(p.right.length == p.dot)
			{
				if(p.left.compareTo("ROOT")!=0)
				{
				
					System.out.print("Adding "+p+" ==>\t");
					p.childExists();
					System.out.println();
					
					prods.add(p);
				}
				else{
					if(i == cols.size()-1)
						System.out.println("ROOT");
				}
			}
		}
	}

/**
 * If string s is present in inGrammar
 * @param s
 * @return
 */
	private final boolean inGrammar(String s)
	{
		boolean found=false;
		Production p;
		
		for(int i=0;i<productions.size();i++)
		{
			p = (Production)productions.get(i);
			for(int j=0;j<p.right.length;j++)
				if(p.right[j].indexOf(s)!=-1)
					found = true;
			
			if(p.left.compareTo(s)==0)
			{
				System.out.println("String contains a non-terminal - cannot parse");
				return false;
			}
		}
		return found;
	}
/**
 * Get the production from the given parameter left
 * @param left
 * @return
 */
	
	private final Vector getProds(String left)
	{
		
		Vector prods = new Vector();	prods.clear();
		Production p;
		
		System.out.println("getProds: "+left);
		getProductionWords(left, prods);
		
		return prods;
	}

	private void getProductionWords(String left, Vector prods) {
		Production p;
		for(int i=0;i<productions.size();i++)
		{
			p = (Production)productions.get(i);
			if(p.left.compareTo(left)==0){
				
				prods.add(p);
			}
		}
	}

	/**
	 * Function checks if a string can parse a sentence
	 * @param sent
	 * @return
	 */
	public final boolean canParse(String sent[])
	{
		
		for(int i=0;i<sent.length;i++)
			if(!inGrammar(sent[i]))
				return false;
		return true;
	}

	
	public void print()
	{
		System.out.println(this.toString());
	}

	/**
	 * Function to convert a word from production to string
	 */
	public String toString()
	{
		String ret = "";
		for(int i=0;i<productions.size();i++)
			ret = ret + ((Production)productions.get(i)).toString() + "\n";
		return ret;
	}
	
	
	public void print(Vector v){
		System.out.println("Printing Vector...");
		for (int i =0; i< v.size();i++){
			Production p = (Production) v.get(i);
			System.out.print(p+" ==>\t");
			p.childExists();
			System.out.println();
			
		}
	}
	
		public void print(HashMap h){
			
			for(Iterator i = h.keySet().iterator(); i.hasNext();) {
			    Production key = (Production)i.next();
			    System.out.print(key+"\t\t:\t\t");
			    Vector  bp  = (Vector) h.get(key);
			    printHashMap(bp);
			    System.out.println();
			}
		}

		private void printHashMap(Vector bp) {
			for (int j =0; j< bp.size();j++){
				Production p = (Production) bp.get(j);
				System.out.print(p.toString()+" ,\t");
			}
		}
}
