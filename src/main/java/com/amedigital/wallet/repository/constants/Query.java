package com.amedigital.wallet.repository.constants;

public class Query {

    public static class Wallet {

        private static final String WALLET_TABLE = "tb_wallet";

        public static final String GET_WALLET_BY_UUID = "SELECT wallet.id, wallet.uuid, wallet.name, wallet.type, wallet.main, wallet.active, " +
                "wallet.created_at, wallet.updated_at, owner.id owner_id, owner.uuid owner_uuid, owner.external_id owner_external_id, " +
                "owner.name owner_name, owner.email owner_email, owner.document owner_document, owner.active owner_active, " +
                "owner.document_type owner_document_type, " +
                "owner.created_at owowner_updated_atner_created_at, owner.updated_at owner_updated_at " +
                "FROM " + WALLET_TABLE + " wallet " +
                "INNER JOIN " + Owner.OWNER_TABLE + " owner on owner_id = owner.id " +
                "WHERE wallet.uuid = :uuid";

        public static final String GET_WALLET_BY_ID = "SELECT wallet.id, wallet.uuid, wallet.name, wallet.type, wallet.main, wallet.active, " +
                "wallet.created_at, wallet.updated_at, owner.id owner_id, owner.uuid owner_uuid, owner.external_id owner_external_id, " +
                "owner.name owner_name, owner.email owner_email, owner.document owner_document, owner.active owner_active, " +
                "owner.document_type owner_document_type, " +
                "owner.created_at owner_created_at, owner.updated_at owner_updated_at " +
                "FROM " + WALLET_TABLE + " wallet " +
                "INNER JOIN " + Owner.OWNER_TABLE + " owner on owner_id = owner.id " +
                "WHERE wallet.id = :id";

        public static final String GET_PRINCIPAL_WALLET_BY_EXTERNAL_OWNER_UUID = "SELECT wallet.id, wallet.uuid, wallet.name, wallet.type, wallet.main, wallet.active, " +
                "wallet.created_at, wallet.updated_at, owner.id owner_id, owner.uuid owner_uuid, owner.external_id owner_external_id, " +
                "owner.name owner_name, owner.email owner_email, owner.document owner_document, owner.active owner_active, " +
                "owner.document_type owner_document_type, " +
                "owner.created_at owner_created_at, owner.updated_at owner_updated_at " +
                "FROM " + WALLET_TABLE + " wallet " +
                "INNER JOIN " + Owner.OWNER_TABLE + " owner on owner_id = owner.id " +
                "WHERE owner.external_id = :uuid and wallet.active is true";


        public static final String UPDATE_WALLET = "UPDATE " + WALLET_TABLE + " SET " +
                "name = :name, " +
                "type = :type, " +
                "main = :main " +
                "WHERE uuid = :uuid";
    }

    public static class Owner {

        private static final String OWNER_TABLE = "tb_owner";

        public static final String UPDATE_OWNER = "UPDATE " + OWNER_TABLE + " SET " +
                "name = :name, " +
                "external_id = :externalId, " +
                "email = :email, " +
                "document = :document, " +
                "document_type = :documentType " +
                "WHERE uuid = :uuid";

        public static final String FIND_OWNER_BY_DOCUMENT = "SELECT owner.id owner_id, owner.uuid owner_uuid, owner.external_id owner_external_id, " +
                "owner.name owner_name, owner.email owner_email, owner.document owner_document, owner.active owner_active, " +
                "owner.document_type owner_document_type, owner.created_at owner_created_at, owner.updated_at owner_updated_at " +
                "FROM " + OWNER_TABLE + " owner " +
                "WHERE owner.document = :document";

        public static final String FIND_OWNER_BY_EMAIL = "SELECT owner.id owner_id, owner.uuid owner_uuid, owner.external_id owner_external_id, " +
                "owner.name owner_name, owner.email owner_email, owner.document owner_document, owner.active owner_active, " +
                "owner.document_type owner_document_type, owner.created_at owner_created_at, owner.updated_at owner_updated_at " +
                "FROM " + OWNER_TABLE + " owner " +
                "WHERE owner.email = :email";

    }
}
