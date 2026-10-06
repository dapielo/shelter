package com.david.shelter.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.david.shelter.exception.FileStorageException;
import com.david.shelter.util.Validator;


// Este servicio es el que se encarga de subir actualizar y crear las imagenes
@Service 
public class FileService {

    private final UserService userService;
    private final Path uploadsDir;

    // Al instanciar el servicio, creamos la carpeta con la ruta configurada en el properties si esta no existe.
    public FileService(@Value("${upload.dir}") String route, UserService userService) {

        this.uploadsDir = Path.of(route);
        this.userService = userService;

        try {
            Files.createDirectories(uploadsDir);
        } catch (IOException e) {
            throw new FileStorageException("Error al intentar crear la carpeta de imagenes");
        }
    }

    // CREATE
    public String storeImage(MultipartFile file) {        
        
        // Si se sube una imagen, extraemos su extension, le ponemos un nombre unico para uqe no haya conflico
        if (file != null && !file.isEmpty()){
            
            // Comprobamos que tenga un nombre valido
            if (!Validator.isValidImage(file)){
                throw new FileStorageException("Ela archivo no tiene un nombre válido");
            }

            // Si no existe creamos una carpeta para el usuario dentro de nuestra carpeta uploads
            String userId = userService.getUserFromContext().getId().toString();
            Path userFolder = uploadsDir.resolve(userId);

            try {
                Files.createDirectories(userFolder);
            } catch (IOException e) {
                throw new FileStorageException("No se ha podido crear la carpeta para el usuario");
            }

            // Obtenemos la extension, cambiamos el nombre por uno unico, y creamos la ruta con el nuevo nombre para copiar los datos de la imagen
            String name = file.getOriginalFilename();
            String extension = name.substring(name.lastIndexOf("."));

            String uniqueName = UUID.randomUUID().toString();
            uniqueName += extension;
            Path imagePath = userFolder.resolve(uniqueName);

            try{
                Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                // Devolvemos la ruta para que se almacene en la informacion de la mascota
                return imagePath.toString();
            } catch (IOException e) {
                throw new FileStorageException("No se ha podido guardar la imagen de la mascota.");
            }

        }
        // Si no se sube imagen, en ese atributo dejamos null;
        return null;
    }

    // READ
    public Resource getImage(String imagePath) {
        
        // Continuar
        if (imagePath == null || imagePath.isBlank()) {
            throw... 
        }

        Path path = Path.of(imagePath);

        try{
            Resource image = new UrlResource(path.toUri());
            return image;
        } catch (IOException e) {
            throw new FileStorageException("No se ha podido cargar la imagen");
        }

    }

    // UPDATE
    public String updateImage(String imagePath, MultipartFile file) {

        // Si no se sube imagen o está vacía no hacemos nada
        if (file == null || file.isEmpty()) {
            return imagePath;
        }

        String newPath = storeImage(file);
        deleteImage(imagePath);

        return newPath;
    }

    // DELETE
    public void deleteImage(String imagePath){

        // Si esta vacio no hacemos nada
        if (imagePath == null || imagePath.isBlank()) {
            return;
        }

        Path root = this.uploadsDir.toAbsolutePath().normalize();
        Path image = Path.of(imagePath).toAbsolutePath().normalize();

        if (!image.startsWith(root) || image.equals(root)) {
            throw new FileStorageException("Ruta de la imagen no válida");
        }

        // Intentamos borrar (puede lanzar excepcion), este servicio no se encarga de determinar si se puede borrar, eso lo tendra que hacer el
        // servicio que lo llame, presumiblemente PetService
        try {
            Files.deleteIfExists(image);        
        } catch (IOException e) {
            throw new FileStorageException("No se ha podido eliminar la imagen");
        }
    }
}
