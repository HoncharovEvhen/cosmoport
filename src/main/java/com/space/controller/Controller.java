package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import exeptions.BadRequestException;
import exeptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
class AllController {
    @Autowired
    ShipService shipService;

    @PostMapping("/rest/ships")
    public ResponseEntity<Ship> myLittleShip(@RequestBody Ship ship) {

        Ship ship1 = shipService.createShip(ship);
        if (ship1 == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(ship1, HttpStatus.OK);
    }


    @GetMapping("/rest/ships")
    public ResponseEntity<List<Ship>> getShipsList(@RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "planet", required = false) String planet,
                                                   @RequestParam(value = "shipType", required = false) ShipType shipType,
                                                   @RequestParam(value = "after", required = false) Long after,
                                                   @RequestParam(value = "before", required = false) Long before,
                                                   @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                                   @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                                   @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                                   @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                                   @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                                   @RequestParam(value = "minRating", required = false) Double minRating,
                                                   @RequestParam(value = "maxRating", required = false) Double maxRating,
                                                   @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
                                                   @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                                   @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        List<Ship> shipsList = shipService.getAllShipsByParams(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        shipsList = shipService.sortListByOrder(shipsList, order);

        shipsList = shipService.getPageByNumber(shipsList, pageNumber, pageSize);

        return new ResponseEntity<>(shipsList, HttpStatus.OK);

    }

    @GetMapping("/rest/ships/count")
    public ResponseEntity<Integer> getShipsCount(@RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "planet", required = false) String planet,
                                                 @RequestParam(value = "shipType", required = false) ShipType shipType,
                                                 @RequestParam(value = "after", required = false) Long after,
                                                 @RequestParam(value = "before", required = false) Long before,
                                                 @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                                 @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                                 @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                                 @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                                 @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                                 @RequestParam(value = "minRating", required = false) Double minRating,
                                                 @RequestParam(value = "maxRating", required = false) Double maxRating) {

        int count = shipService.getAllShipsByParams(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).size();
        return new ResponseEntity<>(count, HttpStatus.OK);

    }

    @GetMapping("/rest/ships/{id}")
    public ResponseEntity<Ship> getShipById(@PathVariable(value = "id") String id) {
        Ship ship;
        try {
            ship = shipService.getShipById(id);

        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(ship, HttpStatus.OK);

    }


    @PostMapping("/rest/ships/{id}")
    public ResponseEntity<Ship> updateShipById(@PathVariable(value = "id")String id, @RequestBody Ship ship){

        Ship ship1;
        try {
            ship1 = shipService.updateShipByTrueId(ship, id);

        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(ship1, HttpStatus.OK);

    }

    @DeleteMapping("/rest/ships/{id}")
    public ResponseEntity<Ship> deleteShipById(@PathVariable(value = "id")String id) {
        try {
            shipService.deleteShipById(id);

        }catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }
}
