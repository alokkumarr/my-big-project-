package com.synchronoss.saw.batch.service.migration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.batch.entities.BisChannelEntity;
import com.synchronoss.saw.batch.entities.dto.BisChannelDto;
import com.synchronoss.saw.batch.entities.repositories.BisChannelDataRestRepository;
import com.synchronoss.sip.utils.Ccode;
import com.synchronoss.sip.utils.SipCommonUtils;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionKeyMigration implements KeyMigration {

  private static final Logger logger = LoggerFactory.getLogger(EncryptionKeyMigration.class);

  @Autowired
  BisChannelDataRestRepository bisChannelRepository;

  @Value("${bis.encryption-key}")
  private String encryptionKey;

  public final SecretKey secretKey = new SecretKeySpec(encryptionKey.getBytes(), "AES");

  @Override
  public void migrate() {
    logger.info("encryptionKey : {}, secretKey : {}", encryptionKey, secretKey);
    List<BisChannelEntity> entities = bisChannelRepository.findAll();
    entities.forEach(entity -> {
      BisChannelDto bisChannelDto = null;
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      JsonNode nodeEntity = null;
      ObjectNode rootNode = null;
      try {
        nodeEntity = objectMapper.readTree(entity.getChannelMetadata());
        rootNode = (ObjectNode) nodeEntity;

        if (rootNode.has("password")) {
          String secretPhrase = rootNode.get("password").asText();
          logger.info("Old Encryption : {}", secretPhrase);
          secretPhrase = this.decryptPassword(secretPhrase);
          secretPhrase = Ccode.cencode(secretPhrase, encryptionKey.getBytes());
          logger.info("New Encryption : {}", secretPhrase);
          rootNode.put("password", secretPhrase);
        }
        bisChannelDto = new BisChannelDto();
        BeanUtils.copyProperties(entity, bisChannelDto);
        bisChannelDto.setChannelMetadata(objectMapper.writeValueAsString(rootNode));
        logger.info("migration for channel : {}", bisChannelDto.getBisChannelSysId());
        if (entity.getCreatedDate() != null) {
          bisChannelDto.setCreatedDate(entity.getCreatedDate().getTime());
        }
        if (entity.getModifiedDate() != null) {
          bisChannelDto.setModifiedDate(entity.getModifiedDate().getTime());
        }
        BisChannelEntity channelEntity = new BisChannelEntity();
        BeanUtils.copyProperties(bisChannelDto, channelEntity);
        channelEntity.setPwdMigrated(1);
        channelEntity = bisChannelRepository.save(channelEntity);
        logger.info("channel : {} updated succesfully", channelEntity.getBisChannelSysId());
      } catch (Exception e) {
        logger.error("Exception while reading the list :", e);
      }
    });
  }

  private String decryptPassword(String encryptedPassword) throws Exception {
    String decryptedPassword = null;

    decryptedPassword = SipCommonUtils.decryptPassword(secretKey, encryptedPassword);
    return decryptedPassword;
  }

  @Test
  public void test() throws Exception {
    encryptionKey = "Saw12345Saw12345";
    SecretKey secKey = new SecretKeySpec(encryptionKey.getBytes(), "AES");
    String prabhu = "Prabhu";
    String obfsuctionEn = SipCommonUtils.encryptPassword(secKey, prabhu);
    logger.info("obfsuctionEn : {}", obfsuctionEn);
    String cCodeEn = Ccode.cencode(prabhu, encryptionKey.getBytes());
    logger.info("cCodeEn : {}", cCodeEn);

    logger
        .info("obsufction decoded pwd : {}", SipCommonUtils.decryptPassword(secKey, obfsuctionEn));
    logger.info("cCode decoded pwd : {}", Ccode.cdecode(cCodeEn, encryptionKey.getBytes()));
  }
}
