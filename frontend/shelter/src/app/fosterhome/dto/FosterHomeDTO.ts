import { PetDTO } from "../../pet/dto/PetDTO";

export interface FosterHomeDTO {
    id? : number;
    name: string;
    capacity: number;
    province: string,
    pets?: PetDTO[]
}