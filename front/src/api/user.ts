import axios from 'axios';
import type { UserEntity }  from "../type/UserEntity";


export async function submit(): Promise<UserEntity> {
    const response = await axios.get<UserEntity>('/api/user/detail');
    return response.data;
}