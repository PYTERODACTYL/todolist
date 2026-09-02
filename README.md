# To-Do List API

API REST para gerenciamento de tarefas.

🔗 **API online:** [https://todolist-8ytk.onrender.com](https://todolist-8ytk.onrender.com/)

## Como testar

Use o [Postman](https://www.postman.com/), o Insomnia ou o `curl` para enviar requisições à API.

### Listar tarefas

```bash
curl GET https://todolist-8ytk.onrender.com/tasks
```

### Criar uma tarefa

```bash
curl -X POST https://todolist-8ytk.onrender.com/tasks \
	-H "Content-Type: application/json" \
	-d '{"title":"Estudar API","description":"Tarefa sobre estudar APIs"}'
```

### Atualizar uma tarefa

Troque `{id}` pelo identificador da tarefa:

```bash
curl -X PUT https://todolist-8ytk.onrender.com/tasks/{id} \
	-H "Content-Type: application/json" \
	-d '{"title":"Estudar API REST","completed":true}'
```


> A primeira requisição pode demorar alguns segundos enquanto o serviço é iniciado no Render.
