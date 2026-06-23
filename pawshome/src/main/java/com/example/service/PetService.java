package com.example.service;

import com.example.model.Mascota;
import com.example.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    @Autowired
    private MascotaRepository petRepository;

    public List<Mascota> findAll() {
        return petRepository.findAll();
    }

    public Mascota findById(Long id) {
        return petRepository.findById(id).orElse(null);
    }

    public Mascota save(Mascota pet) {
        return petRepository.save(pet);
    }

    public void deleteById(Long id) {
        petRepository.deleteById(id);
    }
}
