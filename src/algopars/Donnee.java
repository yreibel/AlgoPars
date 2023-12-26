package algopars;

/**
 * Donnee
 * @version : 1.0,  07 dec. 2018;
 *
 */

public class Donnee
{
	public static final String[] LISTE_TYPE = {"caractere", "entier", "chaine", "reel", "booleen"};
	
	private final String NOM;
	private final String TYPE;
	private final boolean EST_CONSTANTE;
	
	private String valeurActuelle;
	
	private Donnee(String nom, String type, boolean estConstante)
	{
		this.NOM  = nom;
		this.TYPE = type;
		this.EST_CONSTANTE = estConstante;
		
		if (type.equals("caractere")) this.valeurActuelle = "" + (char) 0;
		if (type.equals("entier"   )) this.valeurActuelle = "0";
		if (type.equals("chaine"   )) this.valeurActuelle = "null";
		if (type.equals("reel"     )) this.valeurActuelle = "0,0";
		if (type.equals("booleen"  )) this.valeurActuelle = "false";
		
	}
	
	private Donnee(String nom, String valeur)
	{
		this.NOM  = nom;
		this.EST_CONSTANTE = true;
		
		this.valeurActuelle = valeur;	
		
		if(valeurActuelle.startsWith("'"))                                       this.TYPE = "caractere";
		else if(valeurActuelle.startsWith("\""))                                 this.TYPE = "chaine";    
		else if(valeurActuelle.contains(","))		                             this.TYPE = "reel"; 
		else if(valeurActuelle.equals("true") || valeurActuelle.equals("false")) this.TYPE = "booleen";
		else                                                                     this.TYPE = "entier"; 
		
	}
	
	public static Donnee creerVariable(String nom, String type)
	{
		boolean creer = false;
		
		for (String typeExistant : LISTE_TYPE)
			if (typeExistant.equals(type)) creer = true;
		
		if(creer) return new Donnee(nom ,type, false);
		
		return null;
	}
	
	public static Donnee creerConstante(String nom, String valeur)
	{	
		return new Donnee(nom ,valeur);
	}
	
	public String  getNom()       { return this.NOM;            }
	public String  getType()      { return this.TYPE;           }
	public boolean estConstante() { return this.EST_CONSTANTE;  }
	public String  getValeur()    { return this.valeurActuelle; }
	
	public boolean parDefaut()
	{
		if (this.EST_CONSTANTE) return false;
		
		if (this.TYPE.equals("caractere")) this.valeurActuelle = "" + (char) 0;
		if (this.TYPE.equals("entier"   )) this.valeurActuelle = "0";
		if (this.TYPE.equals("chaine"   )) this.valeurActuelle = "null";
		if (this.TYPE.equals("reel"     )) this.valeurActuelle = "0.0";
		if (this.TYPE.equals("booleen"  )) this.valeurActuelle = "false";
		
		return true;
		
	}
	
	public boolean setValeur(String valeur)
	{
		System.out.print("val:" + valeur);
		
		if (this.EST_CONSTANTE) return false;
		
		if (this.TYPE.equals("chaine") && valeur.startsWith("\""))
		{
			this.valeurActuelle = valeur;
			return true;
		}
		if (this.TYPE.equals("caractere") && valeur.startsWith("'"))
		{
			this.valeurActuelle = valeur;
			return true;
		}
		if (this.TYPE.equals("reel") && valeur.replaceAll("\\.", ",").matches("[0-9]+[,][0-9]+"))
		{
			System.out.print("oui");
			this.valeurActuelle = valeur.replaceAll("\\.", ",");
			return true;
		}
		if (this.TYPE.equals("booleen") && (valeur.equals("true") || valeur.equals("false")))
		{
			this.valeurActuelle = valeur;
			return true;
		}
		if (this.TYPE.equals("entier") && valeur.matches("[\\-]?[0-9]+") && valeur.length() < 10)
		{
			this.valeurActuelle = valeur;
			return true;
		}
		
		return false;
	}
	
	public String toString()
	{
		return this.NOM + " (" + this.TYPE + ") : " + this.valeurActuelle;
	}
}