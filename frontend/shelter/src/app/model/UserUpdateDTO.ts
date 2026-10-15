export interface UserUpdateDTO {
    email?: string,
    username?: string,
    password: string,
    newPassword?: string,
    passwordVerification?: string
}