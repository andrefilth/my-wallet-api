CREATE TABLE IF NOT EXISTS tb_action (
    id BIGINT (30) PRIMARY KEY AUTO_INCREMENT,
    parent_action_id BIGINT (30),
    type ENUM('CREATE', 'AUTHORIZE', 'CAPTURE', 'CANCEL', 'RELEASE', 'MIGRATION', 'REFUND') NOT NULL,
    order_id BIGINT (30),
    order_uuid VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE tb_action ADD FOREIGN KEY fk_order (order_id) REFERENCES tb_order (id);
ALTER TABLE tb_action ADD FOREIGN KEY fk_parent_action (parent_action_id) REFERENCES tb_action (id);

insert into tb_action (id, parent_action_id, type, order_id, order_uuid) values (1, null, 'MIGRATION', null, null)
on duplicate key update type = 'MIGRATION';
commit;