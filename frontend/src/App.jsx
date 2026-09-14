import { useEffect, useState } from 'react'

const toLocalInput = (date) => {
  const offset = date.getTimezoneOffset()
  return new Date(date.getTime() - offset * 60000).toISOString().slice(0, 16)
}

const initialTask = () => {
  const start = new Date()
  start.setMinutes(start.getMinutes() + 30)
  const end = new Date(start)
  end.setHours(end.getHours() + 1)
  return { title: '', description: '', priority: 'NORMAL', startAt: toLocalInput(start), endAt: toLocalInput(end) }
}

function App() {
  const [credentials, setCredentials] = useState(() => sessionStorage.getItem('todo-auth') || '')
  const [screen, setScreen] = useState(() => sessionStorage.getItem('todo-auth') ? 'tasks' : 'login')
  const [tasks, setTasks] = useState([])
  const [message, setMessage] = useState('')

  const request = async (url, options = {}) => {
    const response = await fetch(url, {
      ...options,
      headers: { Authorization: `Basic ${credentials}`, ...(options.headers || {}) },
    })
    if (!response.ok) throw new Error(await response.text() || 'Não foi possível concluir a operação.')
    return response.status === 204 ? null : response.json()
  }

  const loadTasks = async () => {
    try { setTasks(await request('/tasks/')) } catch (error) { setMessage(error.message) }
  }

  useEffect(() => {
    if (credentials) loadTasks()
  }, [credentials])

  const login = async (event) => {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    const auth = btoa(`${data.get('username')}:${data.get('password')}`)
    try {
      const response = await fetch('/tasks/', { headers: { Authorization: `Basic ${auth}` } })
      if (!response.ok) throw new Error('Usuário ou senha inválidos.')
      const listedTasks = await response.json()
      sessionStorage.setItem('todo-auth', auth)
      setCredentials(auth); setTasks(listedTasks); setScreen('tasks'); setMessage('')
    } catch (error) { setMessage(error.message) }
  }

  const register = async (event) => {
    event.preventDefault()
    const data = Object.fromEntries(new FormData(event.currentTarget))
    const confirmPassword = data.confirmPassword
    delete data.confirmPassword

    if (data.password !== confirmPassword) {
      setMessage('As senhas não coincidem.')
      return
    }

    try {
      const response = await fetch('/users/', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      })
      if (!response.ok) throw new Error(await response.text() || 'Não foi possível criar a conta.')

      const auth = btoa(`${data.username}:${data.password}`)
      sessionStorage.setItem('todo-auth', auth)
      setCredentials(auth); setTasks([]); setScreen('tasks'); setMessage('Conta criada. Bem-vindo!')
    } catch (error) { setMessage(error.message) }
  }

  const createTask = async (event) => {
    event.preventDefault()
    const data = Object.fromEntries(new FormData(event.currentTarget))
    try {
      await request('/tasks/', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data) })
      setScreen('tasks'); setMessage('Tarefa criada com sucesso.'); loadTasks()
    } catch (error) { setMessage(error.message) }
  }

  const deleteTask = async (id) => {
    if (!window.confirm('Excluir esta tarefa?')) return
    try { await request(`/tasks/${id}`, { method: 'DELETE' }); setTasks((items) => items.filter((task) => task.id !== id)) }
    catch (error) { setMessage(error.message) }
  }

  const logout = () => { sessionStorage.removeItem('todo-auth'); setCredentials(''); setTasks([]); setScreen('login'); setMessage('') }

  if (screen === 'login' || screen === 'register') return <main className="auth"><section className="card"><p className="eyebrow">TO-DO LIST</p>{screen === 'login' ? <><h1>Organize seu dia.</h1><p>Entre para acessar suas tarefas.</p><form onSubmit={login}><label>Usuário<input name="username" required autoFocus /></label><label>Senha<input name="password" type="password" required /></label><button>Entrar</button></form><p className="account-link">Ainda não tem uma conta? <button className="link" onClick={() => { setScreen('register'); setMessage('') }}>Cadastre-se</button></p></> : <><h1>Crie sua conta.</h1><p>Comece a organizar suas tarefas agora.</p><form onSubmit={register}><label>Nome<input name="name" required autoFocus /></label><label>Usuário<input name="username" required /></label><label>Senha<input name="password" type="password" minLength="6" required /></label><label>Confirmar senha<input name="confirmPassword" type="password" minLength="6" required /></label><button>Criar conta</button></form><p className="account-link">Já tem uma conta? <button className="link" onClick={() => { setScreen('login'); setMessage('') }}>Entrar</button></p></>}{message && <p className="error">{message}</p>}</section></main>

  return <main className="app"><header><div><p className="eyebrow">TO-DO LIST</p><h1>{screen === 'tasks' ? 'Minhas tarefas' : 'Nova tarefa'}</h1></div><button className="secondary" onClick={logout}>Sair</button></header>{message && <p className="notice">{message}</p>}{screen === 'tasks' ? <><div className="toolbar"><span>{tasks.length} {tasks.length === 1 ? 'tarefa' : 'tarefas'}</span><button onClick={() => { setScreen('create'); setMessage('') }}>+ Criar tarefa</button></div><section className="tasks">{tasks.length === 0 ? <p className="empty">Nenhuma tarefa por enquanto. Que tal criar a primeira?</p> : tasks.map((task) => <article className="task" key={task.id}><div><h2>{task.title}</h2>{task.description && <p>{task.description}</p>}<small>{task.startAt && new Date(task.startAt).toLocaleString('pt-BR')} — {task.endAt && new Date(task.endAt).toLocaleString('pt-BR')}</small></div><div className="task-actions"><span className={`priority ${task.priority || 'NORMAL'}`}>{task.priority || 'NORMAL'}</span><button className="danger" onClick={() => deleteTask(task.id)}>Excluir</button></div></article>)}</section></> : <section className="form-card"><button className="back" onClick={() => setScreen('tasks')}>← Voltar</button><form onSubmit={createTask}><label>Título<input name="title" maxLength="100" required autoFocus /></label><label>Descrição<textarea name="description" rows="4" /></label><div className="grid"><label>Início<input name="startAt" type="datetime-local" required defaultValue={initialTask().startAt} /></label><label>Fim<input name="endAt" type="datetime-local" required defaultValue={initialTask().endAt} /></label></div><label>Prioridade<select name="priority" defaultValue="NORMAL"><option value="LOW">Baixa</option><option value="NORMAL">Normal</option><option value="HIGH">Alta</option></select></label><button>Criar tarefa</button></form></section>}</main>
}

export default App
