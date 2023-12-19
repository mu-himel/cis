--INSERT INTO user_credential (id, email_address, password, active)
--VALUES (1,'superadmin@gmail.com','$2a$12$XMG3cM8IzAtwe3NsWn/HRuWmvUJ3YNzDMOCEysSoonoDihx6Dsysi', true);
--
--INSERT INTO users (id, email_address, first_name, last_name, user_credential_id)
--VALUES (1, "superadmin@gmail.com","john","doe",1);


--insert into role(id, role_name) values (1, 'SYS_ADMIN');
--insert into role(id, role_name) values (2, 'USER');
--insert into role(id, role_name) values (3, 'EMPLOYEE');
--insert into role(id, role_name) values (4, 'VENDOR');
--insert into role(id, role_name) values (4, 'ENLISTER');
--insert into role(id, role_name) values (4, 'AUDITOR');
--insert into role(id, role_name) values (5, 'CUSTOMER');

--insert into user_credential_to_role(id, user_credential_id, role_id) values (1, 1, 1);

--insert into role_node (id, name, parent_id, parent_department_id)
--values (1, 'RootRoleNode', null, null);

--insert into department (id, has_user, name, parent_department_id, root_role_node_id, level)
--values (1, 0, 'root', null, 1, 0);
--
--
--insert into user_assignment (id,department_id,parent_user_assignment_id,role_node_id,user_id) values
--    (1,null,null,null,1);


--INSERT INTO modules (id, name,icon,uri,route,module_type,parent_module_access_id, display_order, show_in_menu) VALUES
--                    (4, 'Organizations' ,'dashboard.svg','control-panel/organizations','control-panel/organizations', 'CHILD',null,1,1);
--INSERT INTO modules (id, name,icon,uri,route,module_type,parent_module_access_id, display_order, show_in_menu) VALUES
--                    (5, 'Inventory Control' ,'dashboard.svg','inventory-control','inventory-control', 'CHILD',3,1,1);
--INSERT INTO modules (id, name,icon,uri,route,module_type,parent_module_access_id, display_order, show_in_menu) VALUES
--                    (2, 'Registration' ,'dashboard.svg','vendor-panel/documents-verification','vendor-panel/documents-verification', 'CHILDREN',1,1,1);



                   
                   