package exo4;

import java.util.Stack;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Calculatrice 
{
	// déclaration de la structure de donnée linéaire OperatorStack qui gère les opérateurs
	private Stack<Character> operatorStack;
	// déclaration de la structure de donnée linéaire ValueStack qui gère les opérandes ou valeurs
    private Stack<Double> valueStack;
    // déclaration d'un booléen qui gère les erreurs
    private boolean error;

    // Constructeur
    public Calculatrice() 
    {
        operatorStack = new Stack<Character>();
        valueStack = new Stack<Double>();
        error = false;
    }

    // Fonction qui détermine si l'argument passé en paramètre est un opérateur
    private boolean isOperator(char ch) 
    {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

 // Fonction qui renvoie l'odre de précédence des opérateurs avec le paramètre en caractère
    private int getPrecedence(char ch) 
    {
        if (ch == '+' || ch == '-') 
        {
            return 1;
        }
        if (ch == '*' || ch == '/') 
        {
            return 2;
        }
        return 0;
    }

    // Procèdure qui gère l'expression avec les opérateurs et les opérandes
    private void processOperator(char t) 
    {
        double a, b;
        //Vérification du stack si vide ou non
        if (valueStack.empty()) 
        {
            System.out.println("Erreur expression : pas de valeur");
            error = true;
            return;
        } else 
        {
        	//Renvoi de la valeur en haut de la stack
            b = valueStack.peek();
            //Suppression de l'élément supérieur
            valueStack.pop();
        }
        // Détermine la 2e valeur
        if (valueStack.empty()) 
        {
            System.out.println("Erreur2 expression : pas de valeur");
            error = true;
            return;
        } else 
        {
            a = valueStack.peek();
            valueStack.pop();
        }
        // Calcul de l'expression
        double r = 0;
        if (t == '+') 
        {
            r = a + b;
        } else if (t == '-') 
        {
            r = a - b;
        } else if (t == '*') 
        {
            r = a * b;
        } else if(t == '/') 
        {
            r = a / b;
        } else 
        {
            System.out.println("Erreur opérateur");
            error = true;
        }
        // Insertion du résultat dans le stack
        valueStack.push(r);
    }

    // Procèdure qui gère le contenu du fichier avec l'expression passée en paramètre
    public void getFileContents(String sent) 
    {
       
    	// Tableau de String en supprimant les espaces
        String[] tokens = sent.split(" ");
        // Manipulation du String
        for (int n = 0; n < tokens.length; n++) 
        {      	
            String nextToken = tokens[n];
            
            // Récupération du 1er caractère
            char ch = nextToken.charAt(0);
           
            if ((ch >= '0' && ch <= '9') || ch == '-')
            {
            	// Conversion en Double pour les calculs
                double value = Double.parseDouble(nextToken);
                // Insertion de la valeur dans le stack valueStack
                valueStack.push(value);
            } else if (isOperator(ch)) 
            {
            	// Vérification des opérateurs avec l'ordre des précèdences
                if (operatorStack.empty() || getPrecedence(ch) > getPrecedence(operatorStack.peek())) 
                {
                	// Insertion de l'opérateur dans le stack operatorStack
                    operatorStack.push(ch);
                } else 
                {
                    while (!operatorStack.empty() && getPrecedence(ch) <= getPrecedence(operatorStack.peek())) 
                    {
                        char toProcess = operatorStack.peek();
                        operatorStack.pop();
                        processOperator(toProcess);
                    }
                    operatorStack.push(ch);
                }
            } else if (ch == '(') 
            {
                operatorStack.push(ch);
            } else if (ch == ')') 
            {
                while (!operatorStack.empty() && isOperator(operatorStack.peek())) {
                    char toProcess = operatorStack.peek();
                    operatorStack.pop();
                    processOperator(toProcess);
                }
                if (!operatorStack.empty() && operatorStack.peek() == '(') 
                {
                    operatorStack.pop();
                } else 
                {
                    System.out.println("Erreur de parenthèses");
                    error = true;
                }
            }

        }
        
        while (!operatorStack.empty() && isOperator(operatorStack.peek())) {
            char toProcess = operatorStack.peek();
            operatorStack.pop();
            processOperator(toProcess);
        }
        // Vérification des erreurs
        if (error == false) 
        {
        	
        	double result = valueStack.peek();
        	// Vérification du type du résultat si entier
        	if((result % 1 ) == 0)
        	{
        		int newResult = (int)result;
        		valueStack.pop();
                if (!operatorStack.empty() || !valueStack.empty()) 
                {
                    System.out.println("Erreur expression.");
                } else 
                {
                    System.out.print(newResult);
                }
        	}
        	else
        	{
        		valueStack.pop();
        		if (!operatorStack.empty() || !valueStack.empty()) 
        		{
        			System.out.println("Erreur expression.");
        		} else 
        		{
        			// Affichage du résultat arrondi à 2 chiffres après la virgule
        			System.out.print(Math.round(result * 100.0) / 100.0);
        		}
        	}
        }
    }
	
    // getters et setters
	public Stack<Character> getOperatorStack() {
		return operatorStack;
	}

	public void setOperatorStack(Stack<Character> operatorStack) {
		this.operatorStack = operatorStack;
	}

	public Stack<Double> getValueStack() {
		return valueStack;
	}

	public void setValueStack(Stack<Double> valueStack) {
		this.valueStack = valueStack;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}
	
	// Fonction qui renvoie un String avec le nom du fichier en paramètre
	public static String[] openFile(String s) throws IOException
	{
		Path filePath = new File(s).toPath();
		Stream<String> lines = Files.lines(filePath);
		List<String> result = new ArrayList<String>();
		result = lines.collect(Collectors.toList());
		String[] strLinArray = result.toArray(new String[] {});
		return  strLinArray;
	}
	
	// Fonction affichage du tableau de String passé en paramètre
	public static void display(String[] sa)
	{
		Calculatrice calc = new Calculatrice();
		for(int nbLignes = 0; nbLignes < sa.length; nbLignes++)
		{
			System.out.print(sa[nbLignes] + " = ");
			calc.getFileContents(sa[nbLignes]);
			System.out.println();
		}
			
	}
	
	// Fonction principale
	public static void main(String[] args) throws IOException
	{
		String[] stringsArray;
		stringsArray = openFile("exo4.txt");
		
		// Affichage du texte converti dans la console
		display(stringsArray);
		
		// Création d'un nouveau fichier qui contiendra le texte traduit
		PrintStream o = new PrintStream(new File("exo4Res.txt"));
		
		// Redirection à partir de cette ligne vers le nouveau fichier
		System.setOut(o);
		
		// Affichage du texte converti dans le nouveau fichier
		display(stringsArray);   
	}

}
