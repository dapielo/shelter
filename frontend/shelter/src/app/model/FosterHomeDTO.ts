import { PetDTO } from "./PetDTO";

export interface FosterHomeDTO {
    id? : number;
    name: string;
    capacity: number;
    province: string,
    pets?: PetDTO[]
}