package com.meiorganizadinho.entity;

import com.meiorganizadinho.enums.RoleName;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

    public Role() {}

    public Role(Long id, RoleName name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public static RoleBuilder builder() {
        return new RoleBuilder();
    }

    public static class RoleBuilder {
        private Long id;
        private RoleName name;

        RoleBuilder() {}

        public RoleBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RoleBuilder name(RoleName name) {
            this.name = name;
            return this;
        }

        public Role build() {
            return new Role(this.id, this.name);
        }

        @Override
        public String toString() {
            return "Role.RoleBuilder(id=" + this.id + ", name=" + this.name + ")";
        }
    }
}
