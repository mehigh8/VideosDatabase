# Rosu Mihai Cosmin 323CA

In cadrul implementarii temei am retinut datele initiale (actori, utilizatori,
filme si seriale) intr-o baza de date. Lista cu actiuni am retinut-o in clasa
ActionManager unde are loc si executia acestora.
In functia main stochez datele in baza de date, actiunile in ActionManager si
apoi apelez functia run care incepe executia actiunilor. Inainte sa apelez
functiile corespunzatoare tipului actiunii curente, pregatesc un JSONObject
care retine id-ul actiunii, iar in al doilea element retine mesajul care
rezulta in urma executarii actiunii.

Comenzi:
-Favorite:
  -Pentru a adauga un nou videoclip in lista de favorite a unui utilizator,
  acesta trebuie sa il aiba in lista videoclipurilor vizionate si sa nu il
  aiba deja in lista de favorite. In acest caz comanda va avea succes, altfel
  aceasta va genera eroare.
-View:
  -Aceasta comanda nu poate genera eroare. In cazul in care utilizatorul nu a
  mai vazut videoclipul acesta va fi adaugat in lista cu o vizionare, altfel se
  va incrementa numarul vizionarilor.
-Rating:
  -Pentru a adauga un rating unui videoclip, utilizatorul trebuie sa fi vazut
  videoclipul si sa nu fi lasat deja rating acestui videoclip. In cazul
  serialelor, se poate lasa cate un rating pentru fiecare sezon, motiv pentru
  care trebuie verificat si caror sezoane a lasat rating utilizatorul.

Interogari:
Pentru toate interogarile dupa sortare, in functie de modul de sortare dorit se
adauga in rezultat in ordinea ceruta.
-Actori:
  -Average:
    -Pentru aceasta interogare, actorii sunt sortati dupa rating-ul mediu al
    tuturor filmelor din filmografia acestora, criteriu secundar fiind numele.
    Important este ca nu toate filmele din filmografie au rating, unele fiind
    chiar absente din baza de date, pentru care se considera rating 0 si nu se
    iau in calcul.
  -Awards:
    -Se sorteaza actorii dupa numarul total de premii, criteriu secundar fiind
    numele. Insa nu se iau in calcul cei care nu detin toate premiile din lista
    ceruta.
  -Filter Description:
    -Se sorteaza alfabetic actorii care au in descriere cuvintele mentionate.
    Deoarece cuvintele cerute sunt case insensitive descrierea actorilor
    trebuie transformata in litere mici, si cuvintele cerute nu se pot afla
    ca subsiruri in alte cuvinte, trebuie sa fie prezente singure.
-Utilizatori:
  -Number of ratings:
    -Utilizatorii sunt sortati dupa numarul de rating-uri pe care le-au lasat,
    criteriu secundar fiind username-ul.
-Filme/Seriale:
  -Rating:
    -Se sorteaza filmele/serialele in functie de rating-ul mediu, criteriu
    secundar fiind titlul. Videoclipurile cu rating 0 nu se iau in considerare.
  -Favorite:
    -Se cauta in listele de favorite ale tuturor utilizatorilor din baza de
    date de cate ori se regaseste fiecare videoclip. Apoi acestea sunt sortate
    in functie de acest numar, secundar fiind titlul.
  -Longest:
    -Se sorteaza filmele/serialele in functie de durata, criteriu secundar
    fiind titlul.
  -Most Viewed:
    -Se cauta in listele de videoclipuri vazute ale tuturor utilizatorilor din
    baza de date de cate ori au vazut fiecare videoclip. Apoi, videoclipurile
    sunt sortate in functie de numarul de vizionari, respectiv titlu.

Recomandari:
-Standard:
  -Se cauta in lista de filme, respectiv de seriale, primul videoclip pe care
  utilizatorul nu l-a vazut.
-Best unseen:
  -Se creaza o lista noua cu toate videoclipurile din baza de date. Motivul
  este ca pentru recomandari este necesara ordinea in care au fost introduse
  videoclipurile in baza de date.
  -Se sorteaza aceasta lista in functie de rating, iar rezultatul este primul
  videoclip nevazut de utilizator.
Pentru Popular, Favorite si Search se verifica abonamentul utilizatorului,
deoarece doar cei cu abonament premium pot folosi aceste recomandari.
-Popular:
  -Se creaza o lista cu toate genurile si este sortata in functie de
  popularitate, adica numarul de vizionari ale tuturor videoclipurilor din
  genul respectiv.
  -Dupa ce au fost sortate, se parcurg descrescator de la cel mai popular si se
  cauta primul videoclip nevazut de utilizator.
-Favorite:
  -Se creaza o noua lista cu videoclipuri si se sorteaza dupa numarul de cate
  se regaseste fiecare in listele de favorite ale utilizatorilor. Daca niciun
  utilizator nu are un anumit videoclip in lista de favorite, acel videoclip nu
  se ia in considerare.
  -Dupa sortare se cauta primul videoclip nevazut de utilizator.
-Search:
  -Se creaza o lista noua cu toate videoclipurile care au genul specificat, se
  elimina cele vazute de utilizator si se sorteaza dupa nume.
  -In acest caz raspunsul este o lista de videoclipuri, spre deosebire de
  celelalte recomandari unde rezultatul era un singur videoclip.

Dupa fiecare actiune, se adauga JSONObject-ul in JSONArray care va fi scris
in fisierul de output corespunzator.