# Verziókezelés

A verziókezelő, verziókövető szoftverek lehetővé teszik a munkának a jobb 
koordinálását, és a munkafolyamat figyelését valamint visszanézését.

## A git alapfogalmai:

### Repository

A repository a kódbázist, valamint az ahhoz tartozó history-t, és egyéb
gittel kapcsolatos dolgokat jelöl.

A repository lehet helyi, vagy távoli. 

#### Local Repository

A kódbázis lokális másolata, ami minden fejlesztő gépén megtalálható. Ezen 
fogunk dolgozni, és itt lesznek a változtatások is elmentve.

#### Remote Repository

A kódbázis egy központi, szerveren lévő másolata, ahova a változtatásokat 
feltöltjük. Ennek a neve `origin`.

### Commit

Fájlokon történő változás jelzése a git repository-nak. Egy commit 
tulajdonképpen egy "snapshot" a jelenlegi állapotról, amit megoszthatunk
másokkal. 

Egy commitnak két fontos tulajdonsága van: egy hash érték és egy üzenet.

A commit hash automatikusan generálódik a `git commit` parancs kiadása után.

Az üzenetet mi adjuk meg, itt kell összefoglalni, hogy mit csináltunk a 
commitban.

### Branch

Gitben úgynevezett ágakon, vagy brancheken dolgozunk, ezek külön álló 
fejlesztéseket jelölnek a repositoryban. Egy branch neve általában tartalmazza,
hogy miről van szó könnyebb azonosítás miatt. Gitlabon létrehozott branchek
tartalmazzák a hozzájuk rendelt `Issue` (később lesz róla szó) ID-ját, így 
általában csak a szám alapján hivatkozunk rá. Egy lehetséges branch név:
`93-user-register-function`.

Van egy kiemelt branch, ami minden repositoryban megtalálható. Ezt úgy hívják,
hogy `master`. Ez minden branch "apja", és általában közvetlenül csak a project
manager fogja szerkeszteni, mások számára ez tilos.

### Merge

A felül említett független fejlesztések egyesítése, általában a `master` 
branchre. 

### Merge Conflict

Az egyesítés során előfordulhatnak ütközések, ezeket hívjük merge conflictnak.
Ezeknek a megoldása általában triviális, de vannak komplex esetek is.

## Git használata a gyakorlatban

Mielőtt beleugranánk a használatba, meg kell adnunk a gitnek az email címünket
és a nevünket:

 `git config --global user.name "A Neved"`

 `git config --global user.email "Az email címed"`

Ezen kívül még ajánlott bekapcsolni, hogy a bejelentkezési adatokat elmentse a 
git.

 `git config --global credential.helper store`

A gitet két féleképpen lehet elkezdeni használni: vagy saját repository-t 
készítesz, és ezt töltöd fel (`init`), vagy letöltesz egy létező repository-t
(`clone`).

### git clone 

Egy repository letöltése lokális gépre a következő paranccsal történik:

 `git clone <project url>.git`

Ha kiadjuk ezt a parancsot, a git megfogja kérdezni a gitlab bejelentkező 
adatainkat. 

### git branch

Ág létrehozásához a `git branch` parancsot használjuk. Ezt általában nem kell
lokálisan csinálnunk, a gitlab megoldja nekünk. 

 `git branch example`

 Ez a parancs létre fogja hozni nekünk az `example` branchet.

 `git branch`

 Ez a parancs listázza a jelenlegi brancheket.

    $ git branch
    example
    * master

 Ha aktívvá akarjuk tenni az example branchet, azt a következő paranccsal
 tudjuk megtenni:

 `git switch example`

### git commit

Változtassunk valamit a repository-n. 

 `$ echo This is an example file > example.txt`

Ez létre fog hozni egy example.txt nevű fájlt. Ez a fájl jelenleg a git számára
irreleváns, nem tudja, hogy mit kezdjen vele. Ezt hívják `unstaged` fájlnak.
Ahhoz, hogy a githez hozzáadásra kerüljön a fájl, ki kell adnunk egy `git add`
parancsot.

 `git add example.txt`

Ez több fájl hozzáadásánál hosszú lehet, a `git add .` parancs hozzá 
fog adni minden új fájlt.

Ilyenkor a fájl bekerült egy új állapotba, már `staged` fájlként van 
nyílvántartva. Ezt megtekinthetjük a `git checkout` paranccsal.

    $ git checkout
    A	example.txt

Ilyenkor a git már tudja, hogy ezt a fájlt követni kell majd, de még lényeges
változtatás nem történt a repository-ban. Hogy ez megtörténjen, adjuk ki a
`git commit` parancsot:

    $ git commit -am "added example file"
    [example b648f4c] added example file
    1 file changed, 1 insertion(+)
    create mode 100644 example.txt

### git push

Az előző fájl ekkor belekerült a lokális repository history-ba, de mi azt 
szeretnénk, hogy ez látszódjon a remote-on is. 

Erre van a `git push` parancs. Itt figyelni kell, a használata eltér új
branch és már meglévő branch között!

Ha már meglévő branch-ünk van, akkor az egész `--set-upstream` dolgot 
kihagyhatjuk. Ekkor a parancs csak `git push` lesz.

### git pull

Ha szeretnénk letölteni a remote-on történt változtatásokat, akkor azt
megtehetjük a `git pull` paranccsal. 
Ezt a parancsot csak *tiszta* repository-ban érdemes kiadni.

*tiszta repository* = nem történtek lokális változtatások, vagy azok commit-olva
lettek.

Váltsunk vissza a `master` branchre!

    $ git switch master

Ezután töltsük le a változtatásokat

    $ git pull
    Already up to date.

Mivel a legfrissebb verzió volt lent nálam, ezért semmi nem történt.

#### `git pull` vs `git fetch`

Két módja van annak, hogy letöltsük a változtatásokat. Az egyik a `git pull`,
a másik pedig a `git fetch`. 

A `git fetch` letölti a változtatásokat, de azokat nem integrálja bele a 
jelenlegi "work-tree"-be.

A `git pull` több parancs egyben. Az első része egy `git fetch`, utána pedig
egy `git merge` történik, ami ténylegesen letölti és integrálja a 
változtatásokat a "work-tree"-be.

### git merge

A `git merge <branch>` parancs egyesít két branchet: a jelenlegit a megadottal.

Ezzel a paranccsal a remote-ról is tudunk mergelni, ilyenkor a branch helyett
azt írjuk, hogy `origin/branch`.

    $ git merge origin/master
