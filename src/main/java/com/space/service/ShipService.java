package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import exeptions.BadRequestException;
import exeptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ShipService {

    @Autowired
    private ShipRepository shipRepository;


    public Ship createShip(Ship ship) {

        if (!checkName(ship.getName())) return null;
        if (!checkCrewSize(ship.getCrewSize())) return null;
        if (!checkPlanet(ship.getPlanet())) return null;
        if (!checkProdDate(ship.getProdDate())) return null;
        if (!checkShipTupe(ship.getShipType())) return null;
        if (!checkSpeed(ship.getSpeed())) return null;
        if (ship.getUsed() == null) ship.setUsed(false);

        final int y1 = getProdYear(ship.getProdDate());

        Double rating = (80 * ship.getSpeed() * ((ship.getUsed()) ? 0.5d : 1) / (3019 - y1 + 1));

        ship.setRating(Math.round(rating * 100) / 100d);
        shipRepository.save(ship);

        return ship;
    }

    public List<Ship> getAllShipsByParams(String name,
                                          String planet,
                                          ShipType shipType,
                                          Long after,
                                          Long before,
                                          Boolean isUsed,
                                          Double minSpeed,
                                          Double maxSpeed,
                                          Integer minCrewSize,
                                          Integer maxCrewSize,
                                          Double minRating,
                                          Double maxRating) {

        List<Ship> trueLox = new ArrayList<>();
        Date afterDate = after == null ? null : new Date(after);
        Date beforeDate = before == null ? null : new Date(before);

        for (Ship ship : shipRepository.findAll()) {
            if (name != null && !ship.getName().contains(name)) continue;
            if (planet != null && !ship.getPlanet().contains(planet)) continue;
            if (shipType != null && ship.getShipType() != shipType) continue;
            if (afterDate != null && !ship.getProdDate().after(afterDate)) continue;
            if (beforeDate != null && !ship.getProdDate().before(beforeDate)) continue;
            if (isUsed != null && ship.getUsed().booleanValue() != isUsed.booleanValue()) continue;
            if (minSpeed != null && ship.getSpeed().compareTo(minSpeed) < 0) continue;
            if (maxSpeed != null && ship.getSpeed().compareTo(maxSpeed) > 0) continue;
            if (minCrewSize != null && ship.getCrewSize().compareTo(minCrewSize) < 0) continue;
            if (maxCrewSize != null && ship.getCrewSize().compareTo(maxCrewSize) > 0) continue;
            if (minRating != null && ship.getRating().compareTo(minRating) < 0) continue;
            if (maxRating != null && ship.getRating().compareTo(maxRating) > 0) continue;

            trueLox.add(ship);
        }
        return trueLox;
    }


    public void deleteShipById(String id) {

        if (!checkId(id)) {
            throw new BadRequestException();
        }

        Ship ship = shipRepository.findById(new Long(id)).orElse(null);

        if (ship == null) {
            throw new NotFoundException();
        }

        shipRepository.deleteById(new Long(id));

    }

    public Ship updateShipByTrueId(Ship ship, String id) {

        if (!checkId(id)) throw new BadRequestException();
        if (ship == null) throw new BadRequestException();
        if (shipRepository.findById(new Long(id)).orElse(null) == null) throw new NotFoundException();


        Ship updatedShip = shipRepository.findById(new Long(id)).orElse(null);


            if (ship.getName() != null) {
                if (ship.getName().length() > 50 || ship.getName().length() == 0) throw new BadRequestException();

                updatedShip.setName(ship.getName());
            }


            if (ship.getPlanet() != null) {
                if (ship.getPlanet().length() > 50 || ship.getPlanet().length() == 0) throw new BadRequestException();

                updatedShip.setPlanet(ship.getPlanet());
            }


            if (ship.getShipType() != null) {
                updatedShip.setShipType(ship.getShipType());
            }


            if (ship.getProdDate() != null) {


                int year = getProdYear(ship.getProdDate());
                if (year < 2800 || year > 3019) throw new BadRequestException();

                updatedShip.setProdDate(ship.getProdDate());
            }


            if (ship.getUsed() != null) updatedShip.setUsed(ship.getUsed());


            if (ship.getSpeed() != null) {
                if (ship.getSpeed() < 0.01d || ship.getSpeed() > 0.99d) throw new BadRequestException();

                updatedShip.setSpeed(ship.getSpeed());
            }


            if (ship.getCrewSize() != null) {
                if (ship.getCrewSize() > 9999 || ship.getCrewSize() < 1) throw new BadRequestException();

                updatedShip.setCrewSize(ship.getCrewSize());
            }


            final int y1 = getProdYear(updatedShip.getProdDate());

            Double rating = (80 * updatedShip.getSpeed() * ((updatedShip.getUsed()) ? 0.5d : 1) / (3019 - y1 + 1));

            updatedShip.setRating(Math.round(rating * 100) / 100d);


            shipRepository.deleteById(new Long(id));
            shipRepository.saveAndFlush(updatedShip);

            return updatedShip;
    }


    public int getProdYear(Date date){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);

    }

    public Ship getShipById(String id) {

        if (!checkId(id)) {
            throw new BadRequestException();
        }
        Ship ship = shipRepository.findById(new Long(id)).orElse(null);
        if (ship == null) {
            throw new NotFoundException();
        }

        return shipRepository.findById(new Long(id)).orElse(null);

    }

    public List<Ship> sortListByOrder(List<Ship> ships, ShipOrder order) {

        ships.sort(new Comparator<Ship>() {
            @Override
            public int compare(Ship o1, Ship o2) {
                switch (order) {
                    case ID:
                        return o1.getId().compareTo(o2.getId());
                    case DATE:
                        return o1.getProdDate().compareTo(o2.getProdDate());
                    case SPEED:
                        return o1.getSpeed().compareTo(o2.getSpeed());
                    case RATING:
                        return o1.getRating().compareTo(o2.getRating());
                    default:
                        return 0;
                }
            }
        });

        return ships;

    }

    public List<Ship> getPageByNumber(List<Ship> ships, Integer page, Integer size){

        final int from = page * size;

        int to = from + size;
        if (to > ships.size())
            to = ships.size();

        return ships.subList(from, to);

    }



    protected Boolean checkId(String id){
        Long id1;
        if (id == null || id.equals("") || id.contains(".")) { return false; }
        try{
            id1 = new Long(id);

        }catch (Exception e){ return false; }

        return id1 > 0;
    }


    protected Boolean checkName(String name){

        return name != null && name.length() <= 50 && name.length() != 0;
    }

    protected Boolean checkPlanet(String planet) {

        return planet != null && planet.length() <= 50 && planet.length() != 0;
    }


    protected Boolean checkShipTupe(ShipType shipType){

        return shipType != null;
    }

    protected Boolean checkProdDate(Date date){

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year >= 2800 && year <= 3019;
    }

    protected Boolean checkCrewSize(Integer crewSize){

        return crewSize != null && crewSize <= 9999 && crewSize >= 1;
    }

    protected Boolean checkSpeed(Double speed){

        return speed != null && !(speed < 0.01d) && !(speed > 0.99d);
    }

}
