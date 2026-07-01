DESIGN PATTERNS – TP COMPOSITE

OBJECTIF

●  Mettre en place le pattern composite pour représenter l’organisation hiérarchique d’une

société

INSTRUCTIONS

●  Dans le projet java-design-patterns, créer un package fr.sdv.composite
●  Description du diagramme :

o  Les classes Employe et Service implémente l’interface IElement :

▪  La méthode calculerSalaire() de la classe Service calcule et retourne la
somme des salaires de tous les employés du service ainsi que la somme des salaires
des employés des sous-services associés.

▪  La méthode calculerSalaire() de la classe Employe retourne le salaire de

l’employé.

o  La classe Service contient une liste de IElement pouvant être soit des employés, soit

des services.

●  Implémenter ce modèle objet.
●  Développez une classe exécutable TestComposite

o  Afin de tester le pattern composite, instanciez une petite organisation en respectant

la hiérarchie ci-dessous avec 3 services et 6 employés.

COMMITEZ SUR GITHUB

1

