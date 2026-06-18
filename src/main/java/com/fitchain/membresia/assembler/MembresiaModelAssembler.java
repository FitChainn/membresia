package com.fitchain.membresia.assembler;

import com.fitchain.membresia.controller.MembresiaController;
import com.fitchain.membresia.dto.MembresiaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MembresiaModelAssembler implements RepresentationModelAssembler<MembresiaResponseDTO, EntityModel<MembresiaResponseDTO>> {

    @Override
    public EntityModel<MembresiaResponseDTO> toModel(MembresiaResponseDTO membresia) {
        EntityModel<MembresiaResponseDTO> model = EntityModel.of(membresia,
                linkTo(methodOn(MembresiaController.class).obtenerPorId(membresia.getId())).withSelfRel(),
                linkTo(methodOn(MembresiaController.class).obtenerTodas()).withRel("membresias"),
                linkTo(methodOn(MembresiaController.class).obtenerPorEstado(membresia.getEstado())).withRel("membresias-por-estado")
        );

        if (membresia.getCliente() != null) {
            model.add(linkTo(methodOn(MembresiaController.class)
                    .obtenerPorCliente(membresia.getCliente().getId()))
                    .withRel("membresias-del-cliente"));
        }

        return model;
    }
}