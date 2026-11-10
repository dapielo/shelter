import { PetDTO } from "../../pet/dto/PetDTO";

export interface FosterHomeDTO {
    id? : number;
    name: string;
    province: string,
    pets?: PetDTO[]
}