package in.batur.eksiclone.roleservice.service;

import in.batur.eksiclone.entity.Role;
import in.batur.eksiclone.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<RoleDTO> findAll() {
        log.info("Fetching all roles");
        List<Role> roles = roleRepository.findAll();
        List<RoleDTO> roleDTOs = roles.stream().map(role -> {
            RoleDTO dto = new RoleDTO();
            dto.setId(role.getId());
            dto.setRoleName(role.getRoleName());
            return dto;
        }).collect(Collectors.toList());
        log.info("Found {} roles", roleDTOs.size());
        return roleDTOs;
    }
}