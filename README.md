<H2>Problem Statement</H2>
<H2>OVERVIEW</H4>

A key-value store is a database that stores some value against a given key. The database can be queried using a key to retrieve the value. We want to build an in-memory distributed key-value store.

 

<H4>GOALS</H3> 

1. Users should be able to connect to the database over the network.

2. Users should be able to GET/SET a key. 

3. Users should be able to EXPIRE a key. 

4. Since the database will be distributed, users should be able to connect to any node to SET a key, and connect to any other node to GET the key. 

5. Multiple users should be able to concurrently use the database. 

6. The database should be fully functional even if one node goes down. 

 

<H4>SPECIFICATIONS</H4>

1. The data does not need to be persisted to disk. 

2. The database will have 3 nodes, out of which only one could be down at any time. You can use this to simplify your solution, if you need to. 

3. All nodes will be running on the same machine. 

4. Once a node goes down, you can assume that it will never come up again. You can use this to simplify your solution, if you need to. 

5. You need to submit the code along with a README file about how to build and run the code. 

6. Include a short document in the submission which lists what all you have implemented and the design choices you have made.

 

<H2>CURRENT SOLUTION</H2>

Since, We need not run this model on multiple machines; hence, we’re not working on client server architecture. Instead we’re directly working on a node programme which can be launched multiple times on the same machine to act as multiple nodes accessing the same data warehouse.

Each Node when created would create its own temporary database which would down-sync with the data warehouse in every interval and up-sync based on user preference or every-time a change is initiated in the temporary database . Only one database can be loaded at a node at a time. Upon Disabling a node or switching the database, the node would perform an up-sync. Upon disabling a node, the node would destroy it’s temporary database after up-sync and then the programme would shut down the node. The temporary database concept can later be extended to store the state of a node, so if it ever connects to the Data Warehouse again it may resume from where it left off. 

The Data Warehouse contains multiple JSON files acting as Databases and a special JSON file which contains file names and their HashSets. 

HashSet files are being used to improve syncing, fetching files, deleting files, creating files, etc. Whenever a Node is created, a copy of the HashSet file is stored in the temporary Database such that whenever we use syncs we need not go through every file completely, we can just go through the HashSet File.

We’re using JSON files as databases as they are lightweight and flexible in terms of storing key-value pairs as well as if/when we plan to give this Database a GUI, it’d be easier to work with JSON files.

 

<H3>LOW LEVEL DESIGN</H3>
 

<H4> 1. INTERFACES</H4> 

 <H5>dataBase</H5>

  createDatabase

  getDatabase

  dropDatabase

<H4>2. Classes</H4> 

<H5>1. distributedDatabase implements dataBase</H5>

<p>
  constructors : Upon creation would create a temporary database and store the HashSet File.

  createDatabase: If a database with the same name does not already exist then it creates the database with the given fileName.

  getDatabase: If the database with the given fileName exists at warehouse, it copies it to the temporary database and makes it accessible to read and/or write while removing any other database that may be present in the temporary database.

  dropDatabase: Deletes the current database from the temporary database as well as the data-warehouse. 
</p>

<H5>2. Node extends distributedDatabase</H5> 

<p>
  setKey : If the key does not exist in the database then it creates the key-value pair in the current database else updates the key to the new value entered

  getKey : Prints the key to terminal if the key is present in the current database. 

  deleteKey: Deletes the key from the database, if present. 

  getAll : prints all the key-value pairs present in the current database.

  disableNode : upSyncs the current database, deletes the temporary database and terminates the programme. 
  
</p>

<H5>3. Sync</H5> 

<p>

  upSync: Commits the changes made in the temporary database to the database present at the data-warehouse and updates HashFile based Upon every Change in the current file. If the file in the main db is deleted but exists at some temporary database then while upsyncing it would create a copy of the file in the DataWarehouse.

  downSync: Syncs the changes made by other nodes to the current database at the data-warehouse to the current database at the temporary database and updates the hashFile.
</p>
