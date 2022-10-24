# Proof-Checker
### [Note] Currently only propositional (0th Order) logic is implemented. <a href="#op">See below</a> the current operators and rules.
## Layout
![image](https://user-images.githubusercontent.com/77152357/197525938-e79faabb-eeeb-4c9e-9763-b9aefb8888cf.png)

![image](https://user-images.githubusercontent.com/77152357/197526251-ee9fae67-5f72-46db-b798-6a196e175a2e.png) - Line Number

![image](https://user-images.githubusercontent.com/77152357/197526330-34d5a996-beff-47b3-9dc7-2ea5439d2609.png) - Statement

![image](https://user-images.githubusercontent.com/77152357/197526368-c58eac1c-23fb-4922-81a8-a60cff4d0c00.png) - Arguments

![image](https://user-images.githubusercontent.com/77152357/197526422-bfab8495-bea7-495c-84a8-03134347c0db.png) - Rule

## Navigating
Use the Up and Down arrow keys to move up and down a line respectively. 

Pressing 'Enter' moves down a line and creates a new line when pressed on the last line.

## Inputting a proof
### Statements
In the statement section you can type in a statement using variables and <a href="#op">operators</a>. 
- If the inputted statement is correct, the background of that line will turn green. 
- If the inputted statement is incorrect (does not match <a href="#rules">rule</a>), the background of that line will turn red.
- If the inputted statement is invalid (statement format not obeyed), the background of that line will turn dark red.

Inputting the '|' character will start a new sub proof and deleting it will exit the subproof.

### Inputting <a href="#rules">Rules</a>
A rule consists of two parts: The arguments and the rule itself.

> #### Arguments
- \<line>, \<line>,... - Inputting individual lines
- \<line>-\<line> - Inputting range of lines 

> #### <a href="#rules">Rules</a>
Rules can be chosen using the drop down on the far right of the line. 

## Importing and Exporting
In the menu bar at the top of the application, under 'File' there are the options to Import and Export files.

![image](https://user-images.githubusercontent.com/77152357/197530117-38c21c07-98d2-4c5f-8dc5-81d9c606e5da.png)

Exported files are in .txt format. It is possible to modify the exported proof text file, however, it is not recommended.

## <div id="op">Operators</div>
### These are the currently implemented operators, more will be added if needed

- ^ - Conjunction (AND)
- v - Disjunction (OR)
- ¬ - Negation (NOT)
- => - Implication
- <=> - Bi-Implication

## <div id="rules">Rules</div>
### These are the currently implemented rules, more will be added if needed

- Hyp - Hypothesis
- Sub-Hyp - Sub hypothesis
- Copy - Copy Line
- ^E1 - Conjunction Elimination 1
- ^E2 - Conjunction Elimination 2
- ^I - Conjunction Introduction
- vE - Disjunction Elimination
- vI1 - Disjunction Introduction 1
- v2 - Disjunction Introduction 2
- =>E - Implication Elimination
- =>I - Implication Introduction
- <=>E1 - Bi-Implication Elimination 1
- <=>E2 - Bi-Implication Elimination 2
- <=>I - Bi-Implication Introduction
