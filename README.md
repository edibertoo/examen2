# ToDo API

API for managing ToDos. Made by **Ediberto Sánchez Villalobos**.

# Installation

In order to build the project, you will need to assemble the dependencies and then package the project into a jar:

`mvn assembly:assembly package`

And for executing the server you must run:

`java -jar target/rest-server-1.0-SNAPSHOT-jar-with-dependencies.jar`

# Usage

The API can respond to the following endpoints:

# Get all ToDos

`curl localhost:3000/api/v1/todos`

Responds with the following JSON:

```
[
  {
    "id": 1,
    "title": "Arreglar mi cuarto",
    "completed": false
  }
]
```

![alt text](https://raw.githubusercontent.com/edibertoo/examen2/master/img/1_getAllTodos.PNG)

# Get one ToDo

`curl localhost:3000/api/v1/todos/1`

Responds with the following JSON:

```
{
  "id": 1,
  "title": "Arreglar mi cuarto",
  "completed": false
}
```

![alt text](https://raw.githubusercontent.com/main/rest-server/img/2_getOneTodo.PNG)

# Create ToDo

`curl localhost:3000/api/v1/todos`

Requires the following data:

```
{
	"title":"Ayudar a mi mamá"
}
```

Responds with the following JSON:

```
{
  "mensaje": "El todo fue creado"
}
```

![alt text](https://raw.githubusercontent.com/main/rest-server/img/3_postCreateTodo.PNG)

# Update ToDo

`curl localhost:3000/api/v1/todos/1`

Requires the following data:

```
{
  "completed": true
}
```

Responds with the following JSON:

```
{
  "mensaje": "El todo fue actualizado"
}
```

![alt text](https://raw.githubusercontent.com/main/rest-server/img/4_updateTodo.PNG)

# Delete one ToDo

`curl localhost:3000/api/v1/todos/1`

Responds with the following JSON:

```
{
	'mensaje':'Todo deleted'
}
```

![alt text](https://raw.githubusercontent.com/main/rest-server/img/5_deleteTodo.png)

# Delete all ToDos

`curl localhost:3000/api/v1/todos/all`

Responds with the following JSON:

```
{
	'mensaje':'Se han eliminado todos los todos'
}
```

![alt text](https://raw.githubusercontent.com/main/rest-server/img/6_deleteAllTodos.PNG)
