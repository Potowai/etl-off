DESIGN PATTERNS – TP FACTORY METHOD

OBJECTIF

●  Mettre en place le pattern factory method

INSTRUCTIONS

●  Dans votre projet java-design-patterns, créez un package fr.sdv.factory
●  Dans ce TP on souhaite mettre en place une Factory qui retourne un élément en fonction

d’un type :

o  Les types d’éléments sont les suivants :

▪
▪
▪

Ingrédient
Allergène
Additif

o  Attention, dans ce TP la factory doit prendre en paramètre une énumération et non

un int comme dans le cours.
o  Le modèle objet est le suivant :

1

●  La classe mère Element possède :

o  Un attribut d’instance nom qui représente le nom de l’élément.
o  Un attribut d’instance valeur qui représente la quantité de l’élément présente dans
un produit
o  Un attribut unité qui l’unité de mesure de la masse.
o  Un constructeur qui prend en paramètre les 3 paramètres.

●  Réalisez la méthode de la Factory qui créée une instance d’objet en fonction du type

d’élément passé en paramètre (énumération) et des informations utiles pour instancier l’une des
classes.

●  Créez une classe de tests FactoryTest (avec junit) permettant de tester votre factory.

COMMITEZ SUR GITHUB

2

