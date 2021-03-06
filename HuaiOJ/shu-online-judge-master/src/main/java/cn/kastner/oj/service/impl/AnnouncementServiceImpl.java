package cn.kastner.oj.service.impl;

import cn.kastner.oj.domain.Announcement;
import cn.kastner.oj.domain.User;
import cn.kastner.oj.domain.security.UserContext;
import cn.kastner.oj.dto.AnnouncementDTO;
import cn.kastner.oj.exception.AnnouncementException;
import cn.kastner.oj.repository.AnnouncementRepository;
import cn.kastner.oj.service.AnnouncementService;
import cn.kastner.oj.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

  private final AnnouncementRepository announcementRepository;

  private final DTOMapper mapper;

  @Autowired
  public AnnouncementServiceImpl(AnnouncementRepository announcementRepository, DTOMapper mapper) {
    this.announcementRepository = announcementRepository;
    this.mapper = mapper;
  }

  @Override
  public List<AnnouncementDTO> findAnnouncement(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "modifiedDate");
    List<Announcement> announcements = announcementRepository.findAll(pageable).getContent();
    List<AnnouncementDTO> announcementDTOList = new ArrayList<>();
    for (Announcement announcement : announcements) {
      announcementDTOList.add(mapper.entityToDTO(announcement));
    }
    return announcementDTOList;
  }

  @Override
  public AnnouncementDTO findAnnouncementById(String id) throws AnnouncementException {
    Announcement announcement =
        announcementRepository
            .findById(id)
            .orElseThrow(
                () -> new AnnouncementException(AnnouncementException.NO_SUCH_ANNOUNCEMENT));
    return mapper.entityToDTO(announcement);
  }

  @Override
  public AnnouncementDTO create(AnnouncementDTO announcementDTO) throws AnnouncementException {
    User user = UserContext.getCurrentUser();
    Optional<Announcement> announcementOptional =
        announcementRepository.findByTitle(announcementDTO.getTitle());
    if (announcementOptional.isPresent()) {
      throw new AnnouncementException(AnnouncementException.HAVE_SUCH_ANNOUNCEMENT);
    }
    Announcement announcement = mapper.dtoToEntity(announcementDTO);
    announcement.setAuthor(user);
    announcement.setModifiedDate(LocalDateTime.now());
    return mapper.entityToDTO(announcementRepository.save(announcement));
  }

  @Override
  public AnnouncementDTO update(AnnouncementDTO announcementDTO) throws AnnouncementException {
    User user = UserContext.getCurrentUser();
    Optional<Announcement> announcementOptional =
        announcementRepository.findByTitle(announcementDTO.getTitle());
    if (announcementOptional.isPresent()
        && !announcementOptional.get().getId().equals(announcementDTO.getId())) {
      throw new AnnouncementException(AnnouncementException.HAVE_SUCH_ANNOUNCEMENT);
    }
    Announcement announcement = mapper.dtoToEntity(announcementDTO);
    announcement.setId(announcementDTO.getId());
    announcement.setAuthor(user);
    announcement.setModifiedDate(LocalDateTime.now());
    return mapper.entityToDTO(announcementRepository.save(announcement));
  }

  @Override
  public AnnouncementDTO delete(String id) throws AnnouncementException {
    Announcement announcement =
        announcementRepository
            .findById(id)
            .orElseThrow(
                () -> new AnnouncementException(AnnouncementException.NO_SUCH_ANNOUNCEMENT));
    announcementRepository.delete(announcement);

    return mapper.entityToDTO(announcement);
  }
}
