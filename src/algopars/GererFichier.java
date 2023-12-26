package algopars;

/**
 * GererFichier
 * @version : 1.0,  05 dec. 2018;
 *
 */

import java.util.HashMap;
import java.util.Scanner;
import java.io.FileReader;
import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import algopars.commande.Commande;
import bsh.Interpreter;

public class GererFichier
{	
	private static final int TEMPS_ATTENTE = 1000;
	
	private ArrayList<Donnee> variables;
	private ArrayList<Donnee> variablesASuivre;
	private ArrayList<Donnee> constantes;
	
	private ArrayList<TraceExec>  traceExecution;
	
	private ArrayList<Boolean> conditions;
	private ArrayList<Integer> indexTQ;
	private ArrayList<String>  lignes;
	
	private ArrayList<Commande> listeCommandes;
	private boolean             lectureAuto;
	
	private String couleurLigne;
	
	private Interpreter interpreter;
	
	public GererFichier(String fichier)
	{
		this(fichier, false);
			
	}
	
	public GererFichier(String fichier, boolean lectureAuto)
	{
		this.interpreter    = new Interpreter();
		this.listeCommandes = Commande.preparerCommandes();
		
		this.variables        = new ArrayList<Donnee>();
		this.variablesASuivre = new ArrayList<Donnee>();
		this.constantes       = new ArrayList<Donnee>();
		
		this.traceExecution = new ArrayList<TraceExec>();
		
		this.conditions = new ArrayList<Boolean>();
		this.indexTQ    = new ArrayList<Integer>();
		this.lignes     = new ArrayList<String>();
		
		this.couleurLigne = "BLANC";
		
		this.lireFichier(fichier);
		
		this.lectureAuto = lectureAuto;
	}
	
	private void lireFichier(String fichier)
	{
		
		boolean obtenirConstantes = false;
		boolean obtenirVariables  = false;
		
		Donnee donnee;
		
		try
		{	
        	Scanner sc = new Scanner(new FileReader(fichier));
			
			String ligne;
        	while(sc.hasNextLine())
			{
				ligne = sc.nextLine();
				ligne = ligne.replaceAll("\t", "     ");
				
				this.lignes.add(ligne);
				
				ligne = ligne.replaceAll(" ", "");
				
				
				//Test afin de savoir s'il faut chercher de nouvelles variables / constantes dans les lignes suivantes
				if ( ligne.equals("constante:") ) obtenirConstantes = true;	
				else if ( ligne.equals("variable:") )
				{
					obtenirConstantes = false;
					obtenirVariables  = true;
				} 
				else if ( ligne.equals("DEBUT") ) obtenirVariables = false;	
				
				//Sauvegarde des nouvelles constantes et variables
				else if ( !ligne.equals("variable:") && !ligne.equals("") )
				{	
					if (obtenirConstantes)
					{
						String val = ligne.split("<--")[1];
						
						for (String s : ligne.split("<--")[0].split(","))
						{
							donnee = Donnee.creerConstante(s, val);
							if (donnee != null) this.constantes.add(donnee);
						}
					}
					
					if (obtenirVariables)
					{
						String type = ligne.split(":")[1];
						
						for (String s : ligne.split(":")[0].split(","))
						{
							donnee = Donnee.creerVariable(s, type);
							if (donnee != null) this.variables.add(donnee);
						}
					}
				}
			}
			sc.close();
			
		} catch (Exception e) { e.printStackTrace(); }
		
	}
	
	public void lancerLecture()
	{
		String[] variablesDemandees = AlgoPars.demanderVariablesASuivre().split(" ");
		
		for(int i=0; i<variablesDemandees.length; i++)
			for(int j=i+1; j<variablesDemandees.length;j++)
				if(variablesDemandees[i].equals(variablesDemandees[j]))
					variablesDemandees[j] = "";
		
		for(Donnee donnee : variables)
			for(String nomVariable: variablesDemandees)
				if ( nomVariable.equals(donnee.getNom()) || nomVariable.equals("/all")) this.variablesASuivre.add(donnee);
		
		int ligneAInterpreter = 0;
		int tempAffichage;
		
		for(String ligne : this.lignes)
			if ( ligne.trim().startsWith("DEBUT") ) ligneAInterpreter = this.lignes.indexOf(ligne);
		
		while ( ligneAInterpreter < this.lignes.size() )
		{
			tempAffichage = ligneAInterpreter;
			ligneAInterpreter = interpreterLigne(ligneAInterpreter);
			AlgoPars.genererAffichage(this.traceExecution, tempAffichage, this.couleurLigne);
			
			if (!lectureAuto) ligneAInterpreter = interpreterCommande(AlgoPars.demanderEntree(), ligneAInterpreter, tempAffichage);
			else try { Thread.sleep(TEMPS_ATTENTE); } catch(Exception e) {}
		}
		
		AlgoPars.stopperAffichage();
	}
	
	public int interpreterJusqua(int ligne)
	{
		for(Donnee d: variables) d.parDefaut();
		
		this.traceExecution.clear();
		
		int ligneAInterpreter = 0;
		while( ligneAInterpreter < ligne)
		{
			ligneAInterpreter = interpreterLigne(ligneAInterpreter);
		}
		
		return ligneAInterpreter;
	}
	
	public int interpreterCommande(String commande, int ligneAInterpreter, int ligneEnCours)
	{	
		for(Commande c: this.listeCommandes)
			if ( commande.startsWith(c.getNom())) return c.executer(this, ligneAInterpreter, ligneEnCours, commande);
		
		return ligneAInterpreter;
	}
	
	public int interpreterLigne(int index)
	{	
		String ligne = this.lignes.get(index);
		
		if (ligne.contains("//")) ligne = ligne.substring(0, ligne.indexOf("//"));
		
		ligne = ligne.trim();
		
		if(!this.getSkip()) { this.couleurLigne = "BLANC"; }
			
		if( ligne.startsWith("fsi") )
		{
			this.conditions.remove(this.conditions.size()-1);
		}
		if(ligne.startsWith("sinon"))
		{
			this.conditions.set(this.conditions.size()-1, !this.conditions.get(this.conditions.size()-1).booleanValue());
		}
		if (ligne.startsWith("si") && ligne.endsWith("alors"))
		{
			ligne = ligne.replaceAll("si", "");
			ligne = ligne.replaceAll("alors", "");
			
			this.conditions.add(((Boolean) evaluer(ligne)).booleanValue());
			
			if (this.conditions.get(this.conditions.size()-1)) this.couleurLigne = "VERT";
			else this.couleurLigne = "ROUGE";
		}
		if ( ligne.startsWith("tq") && ligne.endsWith("faire") )
		{
			ligne = ligne.replaceAll("tq ", "");
			ligne = ligne.replaceAll(" faire", "");
			
			this.conditions.add(((Boolean) evaluer(ligne)).booleanValue());
			if (conditions.get(conditions.size()-1)) this.indexTQ.add(index);
			
			if (this.conditions.get(this.conditions.size()-1)) this.couleurLigne = "VERT";
			else this.couleurLigne = "ROUGE";
		}
		if ( ligne.startsWith("ftq") )
		{
			this.conditions.remove(this.conditions.size()-1);
			if (this.indexTQ.size() > 0) return interpreterLigne(this.indexTQ.remove(this.indexTQ.size()-1)-1);
		}
		
		if(this.getSkip())
		{
			return interpreterLigne(index+1);
		}
			
		//Affectation des valeurs
		if ( ligne.contains("<--") )
		{
			ligne = ligne.replaceAll(" ", "");
			String variable = ligne.split("<--")[0];
			
			for (Donnee d : variables)
				if(d.getNom().equals(variable))
				{
					Object resultat = evaluer(ligne.split("<--")[1]);
					if (resultat instanceof String   ) d.setValeur( "\"" + resultat + "\"");
					if (resultat instanceof Boolean  ) d.setValeur( "" + resultat);
					if (resultat instanceof Integer  ) d.setValeur( "" + resultat);
					if (resultat instanceof Character) d.setValeur( "'" + resultat + "'");
					if (resultat instanceof Double   ) d.setValeur(resultat + "");
				}
		}
		if (ligne.startsWith("ecrire"))
		{
			ligne = ligne.substring(ligne.indexOf("(")+1, ligne.lastIndexOf(")"));
			
			this.traceExecution.add(new TraceExec((String) (evaluer(ligne)+""), false));
		}
		if (ligne.startsWith("lire"))
		{
			ligne = ligne.substring(ligne.indexOf("(")+1, ligne.lastIndexOf(")"));
			
			Donnee variableChangee = null;
			for( Donnee d : this.variables)
				if (d.getNom().equals(ligne))
				{
					variableChangee = d;
					while (!d.setValeur(AlgoPars.lireVariable(this.traceExecution, index, this.couleurLigne)));
				}
			
			if (variableChangee != null) this.traceExecution.add(new TraceExec(variableChangee.getValeur(), true));
		}
		
		return index+1;
	}
	
	//Cette methode renvoit false uniquement si tous les boolean de l'arraylist sont true
	private boolean getSkip()
	{
		if (this.conditions.size() == 0) return false;
		
		for(Boolean b: this.conditions)
			if(!b.booleanValue()) return true;
		
		return false;
	}
	
	private Object evaluer(String expression)
	{
		HashMap<String, String> operateurs = new HashMap<String, String>();
		operateurs.put("©", "+\"\"+");
		operateurs.put("=", "==");
		operateurs.put("ET", "&&");
		operateurs.put("OU", "||");
		operateurs.put("non", "!");
		operateurs.put("×", "*");
		operateurs.put("XOU", "^");
		operateurs.put("<=", "<=");
		operateurs.put(">=", ">=");
		operateurs.put("FAUX", "false");
		operateurs.put("VRAI", "true");
		operateurs.put(",", ".");
		operateurs.put("mod", "%");
		operateurs.put("/", "*1.0/");
		operateurs.put("div", "/");
		
		String expFinal = "";
		
		boolean continuer;
		
		while(expression.length() > 0)
		{
			continuer = true;
	
			expression = expression.trim();
			
			Matcher matcherChaine = Pattern.compile("^(\"[^\"]*\")"     ).matcher(expression);
			Matcher matcherEntier = Pattern.compile("^([0-9]+)"         ).matcher(expression);
			Matcher matcherReel   = Pattern.compile("^([0-9]+[,][0-9]+)").matcher(expression);
			Matcher matcherCara   = Pattern.compile("^(\'[^\"]*\')"     ).matcher(expression);
			
			Matcher matcherSqrt = Pattern.compile("^(\\\\/)[(]([^\"'].*?)[)]").matcher(expression);
			Matcher matcherPrim = Pattern.compile("^([a-zA-Z]*)[(](.+?)[)]"  ).matcher(expression);
			
			Matcher matcherPow  = Pattern.compile("^([a-zA-Z0-9]+([,][0-9]+)?)[\\^]([a-zA-Z0-9]+([,][0-9]+)?)").matcher(expression);
			
			
			if (matcherChaine.find())
			{
				expFinal += matcherChaine.group(1);
				expression = expression.substring(matcherChaine.group(1).length());
			}
			else if (matcherPow.find())
			{
				expFinal += Math.pow(Double.parseDouble(evaluer(matcherPow.group(1)) +""), Double.parseDouble(evaluer(matcherPow.group(3)) + ""));
				expression = expression.substring(matcherPow.group(0).length());
			}
			else if (matcherEntier.find())
			{
				expFinal += matcherEntier.group(1);
				expression = expression.substring(matcherEntier.group(1).length());
			}
			else if (matcherReel.find())
			{
				expFinal += matcherReel.group(1).replaceAll(",", ".");
				System.out.print(expFinal);
				expression = expression.substring(matcherReel.group(1).length());
			}
			else if (matcherCara.find())
			{
				expFinal += matcherCara.group(1);
				expression = expression.substring(matcherCara.group(1).length());
			}
			else if (matcherSqrt.find())
			{
				expFinal += gererPrimitive(matcherSqrt.group(1), matcherSqrt.group(2));
				expression = expression.substring(matcherSqrt.group(0).length());
			}
			else if (matcherPrim.find())
			{
				expFinal += gererPrimitive(matcherPrim.group(1), matcherPrim.group(2));
				expression = expression.substring(matcherPrim.group(0).length());
			}
			else
			{
				
				for(String s : operateurs.keySet())
					if(expression.startsWith(s))
					{
						expFinal += operateurs.get(s);
						expression = expression.substring(s.length());
						continuer = false;
					}
				
				for(Donnee d : constantes)
					if(expression.startsWith(d.getNom()))
					{
						if (d.getType().equals("reel")) expFinal += d.getValeur().replaceAll(",", ".");
						else expFinal += d.getValeur();
						expression = expression.substring(d.getNom().length());
						continuer = false;
					}
				
				for(Donnee d: variables)
					if(expression.startsWith(d.getNom()))
					{
						if (d.getType().equals("reel")) expFinal += d.getValeur().replaceAll(",", ".");
						else expFinal += d.getValeur();
						expression = expression.substring(d.getNom().length());
						continuer = false;
					}
				
				if( continuer && expression.length() > 0)
				{
					expFinal += expression.charAt(0);
					expression = expression.substring(1);
				}
				
			}
		}
		
		try
		{
			return this.interpreter.eval(expFinal);
		}
		catch(Exception e) { return null; }
	}
	
	private String gererPrimitive(String primitive, String expression)
	{
		
		String sRet = "";
		
		expression = expression.trim();
		
		if(primitive.equals("enChaine")) 
				sRet += evaluer(expression);
			
		if(primitive.equals("enEntier"))
			sRet += Integer.parseInt(evaluer(expression) + "");
		
		if(primitive.equals("enReel"))
		{
			sRet += Double.parseDouble(evaluer(expression) + "");
		}
		
		if(primitive.equals("ord"))
			sRet += "" + (int) ((char) evaluer(expression));
		
		if(primitive.equals("car"))
			sRet += "'" + (char) Integer.parseInt(evaluer(expression) + "") + "'";
		
		if(primitive.equals("\\/"))
			sRet += Math.sqrt(Double.parseDouble(evaluer(expression) + ""));
		
		if(primitive.equals("plancher"))
			sRet += (int) Double.parseDouble(evaluer(expression) + "");
		
		if(primitive.equals("plafond"))
			sRet += (int) Double.parseDouble(evaluer(expression) + "") +1;
		
		if(primitive.equals("arrondi"))
			sRet += (int) Math.round(Double.parseDouble(evaluer(expression) + ""));

		
		if(primitive.equals("estReel"))
		{
			if(((String) (evaluer(expression)+"")).replaceAll("\\.", ",").matches("[0-9]+[,][0-9]+")) sRet += "true";
			else sRet += "false";
		}
		
		if(primitive.equals("estEntier"))
		{
			if(((String) (evaluer(expression)+"")).matches("[0-9]+")) sRet += "true";
			else sRet += "false";
		}
		
		if(primitive.equals("hasard"))
			sRet += (int) (Math.random()*Integer.parseInt(evaluer(expression) + ""));
		
		System.out.print(sRet);
		return sRet;
	}
	
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getTraceExecution() { return (ArrayList<String>) this.traceExecution.clone(); }
	public int               getNbLignes      () { return this.lignes.size();  }
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getPseudoCode() { return (ArrayList<String>) this.lignes.clone(); }
	@SuppressWarnings("unchecked")
	public ArrayList<Donnee> getConstantes() { return (ArrayList<Donnee>) this.constantes.clone(); }
	@SuppressWarnings("unchecked")
	public ArrayList<Donnee> getVariables () { return (ArrayList<Donnee>) this.variables.clone();  }
	@SuppressWarnings("unchecked")
	public ArrayList<Donnee> getVariablesASuivre() { return (ArrayList<Donnee>) this.variablesASuivre.clone(); }
	@SuppressWarnings("unchecked")
	public ArrayList<Commande> getListeCommandes() { return (ArrayList<Commande>) this.listeCommandes.clone(); }

	public void ajouterVariablesASuivre(Donnee variable)
	{
		if(!this.getVariablesASuivre().contains(variable)) this.variablesASuivre.add(variable);
	}

	public void supprimerVariablesASuivre(Donnee variable)
	{
		if(this.getVariablesASuivre().contains(variable)) this.variablesASuivre.remove(variable);
	}
}
