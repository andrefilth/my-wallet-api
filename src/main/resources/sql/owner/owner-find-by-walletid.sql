SELECT owner.id             as owner_id,
owner.uuid                  as owner_uuid,
owner.external_id           as owner_external_id,
owner.name                  as owner_name,
owner.email                 as owner_email,
owner.document              as owner_document,
owner.active                as owner_active,
owner.document_type         as owner_document_type,
owner.created_at            as owner_created_at,
owner.updated_at            as owner_updated_at
FROM tb_owner owner
JOIN tb_wallet wa ON owner.id = wa.owner_id
WHERE wa.id = :walletId;