package com.fund.asset.web;

import com.fund.asset.entity.PendingAsset;
import com.fund.asset.entity.Position;
import com.fund.asset.repository.PendingAssetRepository;
import com.fund.asset.repository.PositionRepository;
import com.fund.asset.service.AssetTccService;
import com.fund.common.api.Result;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
    private final PositionRepository positions;
    private final PendingAssetRepository pendingAssets;
    private final AssetTccService tcc;

    public AssetController(PositionRepository positions, PendingAssetRepository pendingAssets, AssetTccService tcc) {
        this.positions = positions; this.pendingAssets = pendingAssets; this.tcc = tcc;
    }

    @PostMapping("/pending/tcc/try")
    public Result<Boolean> tryReserve(@Valid @RequestBody PendingAssetRequest request) {
        return Result.success(tcc.tryReserve(request.orderId(), request.userId(), request.productCode(), request.shares()));
    }

    @PostMapping("/pending/{orderId}/confirm")
    @Transactional
    public Result<Void> confirmPending(@PathVariable String orderId) {
        PendingAsset asset = pendingAssets.findByOrderId(orderId)
                .orElseThrow(() -> notFound("在途资产不存在"));
        if ("CONFIRMED".equals(asset.getStatus())) {
            return Result.ok();
        }
        Position position = positions.findByUserIdAndProductCode(asset.getUserId(), asset.getProductCode())
                .orElseGet(() -> new Position(asset.getUserId(), asset.getProductCode(), 0L));
        position.setShares(position.getShares() + asset.getShares());
        positions.save(position);
        asset.setStatus("CONFIRMED");
        pendingAssets.save(asset);
        return Result.ok();
    }

    @PostMapping("/pending/{orderId}/cancel")
    @Transactional
    public Result<Void> cancelPending(@PathVariable String orderId) {
        PendingAsset asset = pendingAssets.findByOrderId(orderId)
                .orElseThrow(() -> notFound("在途资产不存在"));
        if (!"CANCELLED".equals(asset.getStatus()) && !"CONFIRMED".equals(asset.getStatus())) {
            asset.setStatus("CANCELLED");
            pendingAssets.save(asset);
        }
        return Result.ok();
    }

    @GetMapping("/positions") public Result<List<Position>> listPositions() { return Result.success(positions.findAll()); }
    @GetMapping("/positions/{id}") public Result<Position> getPosition(@PathVariable Long id) { return Result.success(position(id)); }
    @PostMapping("/positions") public Result<Position> createPosition(@Valid @RequestBody PositionRequest request) { return Result.success(positions.save(new Position(request.userId(), request.productCode(), request.shares()))); }
    @PutMapping("/positions/{id}") public Result<Position> updatePosition(@PathVariable Long id, @Valid @RequestBody PositionRequest request) {
        Position position = position(id);
        if (!position.getUserId().equals(request.userId()) || !position.getProductCode().equals(request.productCode())) throw badRequest("持仓所属用户和基金代码不可修改");
        position.setShares(request.shares());
        return Result.success(positions.save(position));
    }
    @DeleteMapping("/positions/{id}") public Result<Void> deletePosition(@PathVariable Long id) { positions.delete(position(id)); return Result.ok(); }

    /** Atomically moves redeemable shares into the frozen balance. */
    @PostMapping("/positions/freeze")
    @Transactional
    public Result<Void> freezePositionShares(@Valid @RequestBody PositionRequest request) {
        if (positions.freezeShares(request.userId(), request.productCode(), request.shares()) == 0) {
            throw badRequest("Insufficient redeemable shares");
        }
        return Result.ok();
    }

    @GetMapping("/pending") public Result<List<PendingAsset>> listPendingAssets() { return Result.success(pendingAssets.findAll()); }
    @GetMapping("/pending/{id}") public Result<PendingAsset> getPendingAsset(@PathVariable Long id) { return Result.success(pendingAsset(id)); }
    @PostMapping("/pending") public Result<PendingAsset> createPendingAsset(@Valid @RequestBody PendingAssetRequest request) { return Result.success(pendingAssets.save(new PendingAsset(request.orderId(), request.userId(), request.productCode(), request.shares(), request.status()))); }
    @PutMapping("/pending/{id}") public Result<PendingAsset> updatePendingAsset(@PathVariable Long id, @Valid @RequestBody PendingAssetRequest request) {
        PendingAsset asset = pendingAsset(id);
        if (!asset.getOrderId().equals(request.orderId()) || !asset.getUserId().equals(request.userId()) || !asset.getProductCode().equals(request.productCode())) throw badRequest("在途资产归属字段不可修改");
        asset.setShares(request.shares()); asset.setStatus(request.status());
        return Result.success(pendingAssets.save(asset));
    }
    @DeleteMapping("/pending/{id}") public Result<Void> deletePendingAsset(@PathVariable Long id) { pendingAssets.delete(pendingAsset(id)); return Result.ok(); }

    private Position position(Long id) { return positions.findById(id).orElseThrow(() -> notFound("持仓不存在")); }
    private PendingAsset pendingAsset(Long id) { return pendingAssets.findById(id).orElseThrow(() -> notFound("在途资产不存在")); }
    private ResponseStatusException notFound(String message) { return new ResponseStatusException(HttpStatus.NOT_FOUND, message); }
    private ResponseStatusException badRequest(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
}
