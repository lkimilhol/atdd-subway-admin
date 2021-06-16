package nextstep.subway.section.domain;

import static nextstep.subway.common.ErrorMessage.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new LinkedList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> section.getUpDownStations().stream())
                .collect(Collectors.toList());
    }

    public void updateSection(Station upStation, Station downStation, Distance requestDistance) {
        Section section = getSection(upStation, downStation);
        if (section.isUpStation(upStation)) {
            section.setUpStation(downStation);
        }
        if (section.isDownStation(downStation)) {
            section.setDownStation(upStation);
        }
        section.setDistance(section.calculateDistance(requestDistance));
    }


    private Section getSection(Station upStation, Station downStation) {
        checkDuplicateSectionStations(upStation, downStation);

        return sections.stream()
                .findFirst()
                .filter(section -> section.isContain(upStation) || section.isContain(downStation))
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_STATIONS_SECTION));
    }

    private void checkDuplicateSectionStations(Station upStation, Station downStation) {
        if (isContain(upStation) && isContain(downStation)) {
            throw new IllegalArgumentException(STATIONS_ARE_ALREADY_CONTAINS_SECTION);
        }
    }

    private boolean isContain(Station station) {
        return sections.stream()
                .findFirst()
                .filter(section -> section.isContain(station))
                .isPresent();
    }
}
