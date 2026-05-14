# Vanilla-Wachstumsregeln fuer Minecraft 1.21.1 (Java Edition)

## Zweck und Geltungsbereich

Dieses Dokument fasst die fuer einen Mod relevanten Vanilla-Regeln zusammen, unter denen Pflanzen, Baeume und aehnliche Gewaechse in Minecraft 1.21.1 wachsen, sich ausbreiten oder per Knochenmehl erzwungen werden koennen.

Fokus:

- Java Edition 1.21.1, weil das Mod-Projekt darauf zielt.
- Pflanzen mit echter Wachstums- oder Ausbreitungslogik.
- Knochenmehl, Bienenbestaeubung und Farmer als Beschleuniger, wo relevant.

Nicht im Fokus:

- Reine Dekopflanzen ohne eigene Wachstumslogik.
- Bedrock-exklusive Unterschiede, ausser wenn sie fuer Mod-Design als Stolperfalle wichtig sind.

## Grundregeln

- Die meisten natuerlichen Wachstumsprozesse laufen ueber Random Ticks. Ohne Random Ticks kein natuerliches Wachstum.
- Viele Pflanzen pruefen neben Licht auch Untergrund, freien Raum und gueltige Stuetzbloecke.
- Knochenmehl ueberspringt oft die Lichtpruefung, aber nicht die Pruefung auf gueltigen Untergrund oder genug Platz.
- Viele Pflanzen zerstoeren sich, wenn ihr Stuetzblock entfernt wird, Wasser/Lava in ihren Block laeuft oder ein Piston sie schiebt.
- Farmer koennen in Java Knochenmehl aus vollen Compostern entnehmen und damit Nutzpflanzen schneller wachsen lassen.
- Normale Saplings und Mangroven-Propagulen wachsen wie andere Setzlinge nur, wenn ein Spieler in Reichweite ist, selbst wenn der Chunk geladen bleibt.

## Baeume und baumartige Pflanzen

### Normale Saplings

- Typen in 1.21.1: Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Cherry.
- Normale Platzierung: auf allen Dirt-Varianten ausser Dirt Path sowie auf Moss Block.
- Wachstum ohne Knochenmehl: der Block ueber dem Sapling braucht Lichtlevel 9 oder hoeher.
- Wachstumssystem: zwei versteckte Wachstumsstufen; der dritte erfolgreiche Schritt erzeugt den Baum.
- Knochenmehl: 45% Chance, den Sapling um genau eine Wachstumsstufe weiterzubringen, falls der Baum unter den aktuellen Bedingungen ueberhaupt wachsen koennte.
- Knochenmehl wird verschwendet, wenn ein Baum wegen falschem Untergrund oder fehlendem Platz keinerlei gueltige Wachstumschance hat.
- Zusaetzliche Zerstorungsregeln: Saplings droppen, wenn ihr Stuetzblock entfernt oder bewegt wird, Wasser oder Lava in ihren Block fliesst oder ein Piston sie schiebt.

Platzbedarf pro Baumtyp:

- Oak: mindestens 5 freie Bloecke nach oben in einer 3x3-Saeule. Wenn ein Block im Wachstumsraum liegt, aber nicht direkt ueber dem Sapling, kann der Baum trotzdem wachsen und wird in eine grosse Variante gezwungen.
- Birch: mindestens 6 freie Bloecke nach oben in einer 3x3-Saeule.
- Spruce, einzeln: mindestens 6 freie Bloecke nach oben in einer 5x5-Saeule.
- Giant Spruce, 2x2: mindestens 14 freie Bloecke nach oben; in Java muss der 5x5-Bereich um den nordwestlichen Sapling und ein freier Bereich auf Sapling-Hoehe passen.
- Jungle, einzeln: mindestens 5 freie Bloecke nach oben in einer 3x3-Saeule.
- Giant Jungle, 2x2: mindestens 11 freie Bloecke nach oben; auf Sapling-Hoehe muss ein gueltiger Freiraum bestehen. Logs, Wood und Leaves sind dabei toleranter als andere Blocktypen.
- Acacia: mindestens 6 freie Bloecke nach oben in einer 5x5-Saeule.
- Dark Oak, 2x2 zwingend: mindestens 7 freie Bloecke nach oben. Ein einzelner Dark-Oak-Sapling kann nie zu einem Baum werden.
- Cherry: mindestens 8 freie Bloecke nach oben in einer 5x5-Saeule.

Wichtige 2x2-Sonderfaelle:

- Dark Oak braucht immer 4 Saplings im 2x2-Muster.
- Spruce und Jungle koennen an 2x2-Anordnungen scheitern, wenn sie Teil einer ungueltigen oder mehrdeutigen 2x2-Gruppe sind.
- Nahegelegene Bloecke auf gleicher Hoehe stoeren die meisten Baumtypen nicht, Giant Jungle und Giant Spruce haben hier aber strengere Regeln.

Bee-Nest-Sonderfall:

- Oak, Birch und Cherry, die aus Saplings wachsen, haben 5% Chance auf ein Bee Nest mit 2 bis 3 Bienen, wenn innerhalb von 2 Bloecken eine Blume steht. In Java zaehlen auch Flowering Azalea und Flowering Azalea Leaves.

### Mangrove Propagule

- Es gibt haengende und nicht haengende Propagulen.
- Eine haengende Propagule kann nie direkt zu einem Baum werden.
- Nicht haengende Propagulen verhalten sich wie Saplings und haben ebenfalls zwei versteckte Wachstumsstufen.
- Platzierbar auf Dirt-Varianten ausser Dirt Path sowie auf Moss Block, Mud und Clay.
- Kann auch unter Wasser platziert und zu einem Baum herangezogen werden.
- Natuerliches Wachstum: Lichtlevel 9 oder hoeher.
- Knochenmehl auf die nicht haengende Propagule: 45% Chance auf die naechste Wachstumsstufe, auch ohne ausreichendes Licht.
- Knochenmehl auf Mangrove Leaves mit freiem Block darunter: erzeugt eine haengende Propagule.
- Knochenmehl auf haengende Propagule: erhoeht nur deren Alter um 1, kein direktes Baumwachstum.

Raum- und Wurzelregeln fuer den Baum:

- Mindestens 6 freie Bloecke oberhalb der Propagule.
- Die Wurzeln duerfen sich bis zu 5 Bloecke horizontal ausbreiten.
- Es muss mindestens ein fester Block in dem 9x9x9-Raum um und unter der Propagule existieren, ausserhalb der senkrechten Stammspalte.
- Es muessen geeignete feste Bloecke als Landeplaetze fuer die Wurzelenden innerhalb von 11 Bloecken unterhalb der Propagule in einem 9x9-Bereich vorhanden sein.
- Mud zaehlt dabei nicht als fester Block.
- Dirt ueber der Propagule blockiert das Wachstum nicht. In Java werden auch Logs, Wood und deren stripped Varianten ueber der Propagule toleriert.

### Azalea

- Azalea waechst nicht von selbst wie ein normaler Sapling.
- Platzierbar auf Grass Block, Dirt, Coarse Dirt, Rooted Dirt, Podzol, Moss Block, Farmland, Mud, Muddy Mangrove Roots und Clay.
- Der einzige Wachstums-Trigger ist Knochenmehl.
- Knochenmehl hat 45% Chance, aus der Azalea einen Azalea-Baum zu machen, wenn ueber ihr ein im Wesentlichen freier 3x5x3-Raum vorhanden ist.
- Fehlt dieser Platz, entsteht kein Baum.

## Farmland und klassische Feldfruechte

### Farmland

- Hydriert durch Wasser bis 4 Bloecke horizontal entfernt, solange das Wasser auf derselben Hoehe oder 1 Block hoeher liegt.
- Regen hydriert ebenfalls.
- Trockenes, leeres Farmland wird wieder zu Dirt.
- Farmland kann auch durch Trampeln, durch einen festen Block direkt ueber ihm oder durch bestimmte Piston-Interaktionen wieder zu Dirt werden.
- Wenn Farmland unter einer Pflanze ungueltig wird, bricht die Pflanze und droppt ihre normalen Drops.

### Wheat, Carrots, Potatoes, Beetroots

Gemeinsame Grundregeln:

- Brauchen Farmland als Untergrund.
- Wenn Farmland zu Dirt wird oder die Crop von einem Piston ungueltig gemacht wird, bricht die Pflanze.
- Farmer koennen diese Crops mit Knochenmehl beschleunigen.

Einzelregeln:

- Wheat: braucht in Java Lichtlevel 9 zum Wachsen. Wird ein Seed ohne Licht gepflanzt, zerstoert er sich in Java. Knochenmehl laesst Wheat wie andere Standard-Crops 2 bis 5 Wachstumsstufen reifen.
- Carrots: in Java Lichtlevel 8 zum Pflanzen, Lichtlevel 9 zum natuerlichen Wachsen. Knochenmehl reift 2 bis 5 Stufen.
- Potatoes: in Java Lichtlevel 8 zum Pflanzen, Lichtlevel 9 zum natuerlichen Wachsen. Knochenmehl reift 2 bis 5 Stufen.
- Beetroots: Knochenmehl hat 75% Chance, genau die naechste Wachstumsstufe auszulosen. Die konsultierten Fragmente bestaetigen das normale Farmland- und Crop-Verhalten; der explizit sichtbare Sonderpunkt ist hier die andere Knochenmehl-Logik im Vergleich zu Wheat, Carrots und Potatoes.

### Melon Stem und Pumpkin Stem

- Seeds koennen nur auf Farmland gepflanzt werden.
- Natuerliches Stem-Wachstum braucht Lichtlevel 9 oder hoeher.
- Knochenmehl reift nur den Stem selbst um 2 bis 5 Wachstumsstufen.
- Knochenmehl erzeugt niemals direkt eine Melon oder Pumpkin.
- Ein voll ausgereifter Stem erzeugt seine Frucht nur, wenn mindestens ein benachbarter Block ein gueltiger Boden ist, z. B. Dirt, Coarse Dirt, Rooted Dirt, Grass Block, Farmland, Podzol, Mycelium, Moss Block, Mud oder Muddy Mangrove Roots.
- Existiert bereits eine benachbarte Melon oder Pumpkin, erzeugt derselbe Stem keine weitere, bis die vorhandene Frucht entfernt wird.

### Torchflower Crop

- Torchflower Seeds werden auf Farmland gepflanzt.
- Der Crop hat 3 Wachstumsstufen.
- Wird der Crop vor der letzten Stufe gebrochen, droppt der Seed. Die letzte Stufe droppt die Torchflower selbst und nicht mehr den Seed.
- Knochenmehl waechst jede Stufe des Torchflower Crop weiter.
- Wenn Farmland zerstoert, zu Dirt wird oder ein Piston den Crop ungueltig macht, bricht die Pflanze.
- Farmer koennen die Seeds pflanzen, den ausgereiften Crop aber nicht ernten.

### Pitcher Crop

- Pitcher Pods werden auf Farmland gepflanzt.
- Der Crop hat 5 Wachstumsstufen.
- Vor Alter 4 droppt der Crop beim Zerstoeren wieder einen Pitcher Pod; auf Alter 4 droppt er die fertige Pitcher Plant.
- Knochenmehl waechst jede Stufe des Pitcher Crop weiter.
- In Java koennen Bienen Pitcher Crops ausdruecklich nicht fertilisieren.
- Wenn Farmland zerstoert oder zu Dirt wird, bricht der Crop.
- Pistons zerstoeren Pitcher Crops; sie lassen sich nicht mit Sticky Pistons ziehen.
- Pitcher Crops auf Alter 0 koennen zertrampelt werden, auf Alter 1 bis 4 nicht mehr.
- Farmer koennen Pitcher Pods pflanzen, aber nicht ernten.

## Vertikal wachsende und kletternde Pflanzen

### Bamboo

- Platzierbar auf vielen natuerlichen Bodenarten, darunter Grass Block, Dirt, Coarse Dirt, Rooted Dirt, Podzol, Gravel, Sand, Red Sand, Mycelium, Moss Block, Suspicious Sand, Suspicious Gravel, Mud und Muddy Mangrove Roots.
- Natuerliches Wachstum erfolgt nach oben bis typischerweise 12 bis 16 Bloecke Hoehe.
- Das oberste Bamboo-Segment braucht Lichtlevel 9 oder hoeher, um weiterzuwachsen.
- Knochenmehl laesst Bamboo um 1 bis 2 Segmente wachsen.
- Ist der Weg nach oben blockiert, wird Knochenmehl verschwendet.

### Sugar Cane

- Platzierbar auf geeigneten Dirt- und Sand-Varianten, solange der Pflanzblock direkt an Wasser grenzt.
- Waechst unabhaengig vom Licht.
- Natuerliche Maximalhoehe: 3 Bloecke.
- Knochenmehl hat in Java keine Wirkung.
- Wenn die Wasser-Nachbarschaft oder der Untergrund ungueltig wird, bricht Sugar Cane.

### Cactus

- Platzierbar nur auf Sand, Red Sand, Suspicious Sand oder auf einem anderen Cactus.
- Kein horizontal angrenzender fester Block und keine horizontale Lava-Nachbarschaft erlaubt; sonst bricht der Cactus.
- Licht ist fuer das Wachstum egal.
- Natuerliche Maximalhoehe: 3 Bloecke.
- Knochenmehl hat keine Wirkung.

### Cocoa

- Platzierung nur an den Seiten von Jungle Logs oder Jungle Wood, jeweils auch stripped Varianten.
- 3 Wachstumsstufen.
- Natuerliches Wachstum ueber Random Ticks.
- Knochenmehl reift genau 1 Wachstumsstufe.
- Wird der tragende Log entfernt, Wasser in den Block gespuelt oder ein Piston benutzt, bricht Cocoa.

### Sweet Berry Bush

- Platzierbar auf Grass Block, Dirt, Coarse Dirt, Rooted Dirt, Farmland, Podzol, Mycelium, Moss Block, Mud und Muddy Mangrove Roots.
- 4 Wachstumsstufen.
- Natuerliches Wachstum braucht Lichtlevel 9 oder hoeher.
- Ein voller, nicht transparenter Block direkt ueber dem Bush verhindert natuerliches Wachstum.
- Knochenmehl reift den Bush um 1 Wachstumsstufe, auch wenn Licht oder Freiraum fuer natuerliches Wachstum gerade nicht passen.
- Bienen mit Pollen koennen den Bush ebenfalls um 1 Wachstumsstufe fertilisieren.

### Cave Vines / Glow Berries

- Glow Berries werden an die Unterseite der meisten Bloecke mit vollflaechiger Unterseite gesetzt oder an die Unterseite einer vorhandenen Cave Vine.
- Keine Lichtanforderung fuer natuerliches Wachstum.
- Wachstum nach unten mit 10% Chance pro Random Tick.
- Stoppt, wenn der Boden erreicht ist, wenn die unterste Vine Alter 25 erreicht oder wenn die Spitze mit Shears beschnitten wird.
- Knochenmehl verlaengert die Vine nicht.
- Knochenmehl auf Cave Vines erzeugt nur Glow Berries, falls der getroffene Vine-Block noch keine traegt.
- Bienen mit Pollen koennen Cave Vines ebenfalls fertilisieren und Glow Berries wachsen lassen.
- Nur neu erzeugte Vine-Segmente haben natuerlich eine Chance, berries-tragend zu entstehen; bestehende leere Segmente fuellen sich nicht von selbst spaeter mit Beeren.
- Wird der Stuetzblock entfernt, Wasser hineingespuelt oder ein Piston verwendet, bricht die Vine.

### Weeping Vines

- Platzierbar an der Unterseite eines Blocks.
- Natuerliches Wachstum verlaeuft nach unten.
- 10% Chance pro Random Tick auf genau 1 neues Segment.
- Stoppt bei Alter 25, bei fehlendem Freiraum nach unten oder wenn die Spitze mit Shears beschnitten wurde.
- Knochenmehl verlaengert die gesamte Kette um einige Bloecke nach unten und kann auf jedem Segment der Kette benutzt werden.
- Keine Lichtanforderung.

### Twisting Vines

- Aufwaerts wachsende Nether-Vine.
- Natuerliches Wachstum verlaeuft nach oben mit 10% Chance pro Random Tick.
- Stoppt bei Alter 25 oder wenn kein Freiraum nach oben mehr existiert.
- Knochenmehl laesst die Vine sofort einige Bloecke weiterwachsen.
- Knochenmehl auf Warped Nylium kann zusaetzlich neue 1-Block-hohe Twisting Vines in der Umgebung erzeugen.
- Segmente ohne Stuetzblock darunter brechen.
- Keine Lichtanforderung.

### Normale Vines

- Wachsen an Waenden und koennen sich per Block Tick ausbreiten.
- Pro Block Tick besteht 25% Chance auf einen Ausbreitungsversuch in eine zufaellige Richtung.
- Sie koennen nach unten, seitlich und unter bestimmten Bedingungen nach oben wachsen.
- Knochenmehl hat auf normale Vines keine Wirkung.
- In Java kann ihre Ausbreitung ueber die Game Rule doVinesSpread deaktiviert werden.

## Pilze, Wasserpflanzen, Nether- und End-Pflanzen

### Mushrooms

- Natuerliche Platzierung auf normalen Vollflaechen nur bei Licht unter 13 und nicht direkt unter offenem Himmel.
- Auf Mycelium, Podzol und Nylium koennen Mushrooms unabhaengig vom Licht bestehen.
- Natuerliche Ausbreitung: 4% Chance pro Random Tick, solange weniger als 5 Mushrooms derselben Art in einem 9x9x3-Bereich existieren.
- Knochenmehl hat 40% Chance, einen Huge Mushroom zu erzeugen, falls der aktuelle Block ein gueltiger Huge-Mushroom-Grow-Block ist und genug Platz vorhanden ist.
- Knochenmehl auf einem Mushroom auf ungueltigem Grow-Block ist ein expliziter Waste-Fall.

### Kelp

- Muss unter Wasser platziert sein, in Water Source oder nach unten fliessendem Wasser.
- Keine Lichtanforderung.
- Natuerliches Wachstum erfolgt nach oben, bis das Alterslimit erreicht ist.
- Knochenmehl verlaengert Kelp pro Einsatz um 1 Block.

### Sea Pickle

- Natuerliches Wachstum oder Ausbreitung findet ohne Knochenmehl praktisch nicht statt.
- Knochenmehl funktioniert nur unter Wasser und auf lebendem Coral Block.
- Bei gueltigen Bedingungen vergroessert Knochenmehl die Pickle-Gruppe und erzeugt zusaetzliche Sea Pickles auf benachbarten Coral Blocks.
- Knochenmehl wird verschwendet, wenn der Pickle nicht auf Coral Block steht oder bereits 4 Pickles im Cluster existieren und keine gueltigen Ausbreitungsziele vorhanden sind.

### Nether Wart

- Platzierbar nur auf Soul Sand, nicht auf Soul Soil.
- Waechst in jeder Dimension.
- Licht spielt keine Rolle.
- Knochenmehl hat keine Wirkung.

### Nether Fungi

- Crimson Fungus und Warped Fungus koennen auf mehreren Nether-Untergruenden platziert werden.
- Fuer das Wachstum zum Huge Fungus ist aber das jeweils passende Nylium zwingend: Crimson Fungus auf Crimson Nylium, Warped Fungus auf Warped Nylium.
- Knochenmehl auf passendem Nylium: 40% Chance auf Huge Fungus.
- Knochenmehl auf unpassendem Untergrund ist ein expliziter Waste-Fall.
- Knochenmehl auf Nylium kann zusaetzlich Roots, Sprouts und weitere Fungi in der Umgebung erzeugen. Auf Warped Nylium koennen dabei auch Twisting Vines entstehen.

### Chorus Flower

- Nutzt eine eigene Wachstumslogik mit End Stone und Chorus Plant als Support-System.
- Waechst in jede Dimension und braucht kein Licht.
- Waechst nach oben und verzweigt sich dann seitlich weiter.
- Alter 5 ist der tote Endzustand der Chorus Flower.
- Knochenmehl hat keine Wirkung.

## Knochenmehl: wichtige Sonderfaelle und Waste-Faelle

Explizit dokumentierte Waste-Faelle in 1.21.1:

- Saplings oder nicht haengende Mangrove-Propagulen, die wegen Untergrund oder Platz gar keine Chance auf Baumwachstum haben.
- Einzelne Dark-Oak-Saplings, weil Dark Oak immer 2x2 braucht.
- Mushrooms auf ungueltigem Huge-Mushroom-Grow-Block.
- Sea Pickles ohne Coral Block oder ohne gueltige Ausbreitungsziele.
- Bamboo, wenn der Weg nach oben blockiert ist.
- Nether Fungus ausserhalb des passenden Nylium.
- Pflanzen, deren Wachstum an der Build Limit scheitert.

Explizit dokumentierte Nicht-Effekte:

- Cactus: keine Wirkung.
- Nether Wart: keine Wirkung.
- Sugar Cane in Java: keine Wirkung.
- Chorus Plant / Chorus Flower: keine Wirkung.
- Normale Vines: keine Wirkung.
- Cave Vines: Knochenmehl erzeugt nur Glow Berries, aber kein neues Vine-Segment.
- Melon/Pumpkin-Stems: Knochenmehl reift nur den Stem, nie direkt die Frucht.

## Relevante andere Beschleuniger ausser Knochenmehl

- Farmer: beschleunigen Crops ueber Knochenmehl aus Compostern.
- Bienen: koennen Sweet Berry Bushes um 1 Stufe reifen lassen.
- Bienen: koennen Cave Vines dazu bringen, Glow Berries zu tragen.
- Bienen: koennen Pitcher Crops in Java ausdruecklich nicht fertilisieren.
- Mangrove Propagules interagieren mit Bienen, aber das ist Honig- bzw. Pollinationsverhalten und kein Baumwachstums-Boost.

## Praktische Ableitungen fuer den Mod

- Es reicht nicht, nur Licht zu pruefen. Viele Pflanzen haengen an Support- oder Raumregeln.
- Bei Baeumen muessen Untergrund, Hoehe und Kollisionsraum getrennt behandelt werden.
- Knochenmehl sollte im Mod nicht generell als "ignore all restrictions" modelliert werden. In Vanilla ignoriert es haeufig Licht, aber fast nie Platz oder Support.
- Fuer vertikale Pflanzen lohnt sich ein eigener Regeltyp: nach oben, nach unten, fruchttragend oder nur stage-based.
- Fruchttragende Crops wie Melon/Pumpkin sollten getrennt in Stem-Wachstum und Frucht-Spawning modelliert werden.

## Quellenbasis

Die Zusammenfassung basiert auf den folgenden Minecraft-Wiki-Seiten, jeweils auf die fuer 1.21.1 relevanten Aussagen heruntergebrochen:

- Bone Meal
- Sapling
- Mangrove Propagule
- Azalea
- Farmland
- Wheat
- Carrots
- Potatoes
- Beetroot
- Melon Stem
- Pumpkin Stem
- Torchflower Seeds / Torchflower Crop
- Pitcher Pod / Pitcher Crop
- Bamboo
- Sugar Cane
- Cactus
- Cocoa
- Sweet Berry Bush
- Glow Berries / Cave Vines
- Weeping Vines
- Twisting Vines
- Vines
- Mushroom
- Kelp
- Sea Pickle
- Nether Wart
- Nether Fungus
- Chorus Flower