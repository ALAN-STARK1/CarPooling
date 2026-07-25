export interface Result<T = any> {
    code: number;
    msg: string;
    data: T;
}

export interface UserVO{
    id:string;
    username: string;
    avatar: string;
    token: string;
}