// Esta interfaz define como tiene que ser un objeto que recibimos en una peticion a la API
export interface UserSession {

    id : number,
    username: string,
    token: string
}